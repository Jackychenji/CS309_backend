package cs309.dormiselect.backend.controller

import cs309.dormiselect.backend.config.CurrentAccount
import cs309.dormiselect.backend.data.*
import cs309.dormiselect.backend.data.dormitory.*
import cs309.dormiselect.backend.data.student.StudentInfoDto
import cs309.dormiselect.backend.data.student.StudentListDto
import cs309.dormiselect.backend.data.student.StudentUploadDto
import cs309.dormiselect.backend.data.teacher.AnnouncementPublishDto
import cs309.dormiselect.backend.data.teacher.TeamMemberRemoveDto
import cs309.dormiselect.backend.domain.Announcement
import cs309.dormiselect.backend.domain.Dormitory
import cs309.dormiselect.backend.domain.Team
import cs309.dormiselect.backend.domain.account.Account
import cs309.dormiselect.backend.domain.account.Student
import cs309.dormiselect.backend.domain.notification.NewAnnouncementNotification
import cs309.dormiselect.backend.repo.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*
import kotlin.jvm.optionals.getOrElse


@RestController
@RequestMapping("/api/teacher")
class TeacherController(
    private val studentRepo: StudentRepo,
    private val dormitoryRepo: DormitoryRepo,
    private val accountRepo: AccountRepo,
    private val teamRepo: TeamRepo,
    private val teacherRepo: TeacherRepo,
    private val commentRepo: CommentRepo,
    private val announcementRepo: AnnouncementRepo, private val notificationRepo: NotificationRepo,
) {
    @GetMapping("/select/list")
    fun viewTeamSelection(
        @ModelAttribute body: PageInfo,
    ): RestResponse<PageResult<Any>?> {
        val (page, pageSize) = body
        val pageable: Pageable = PageRequest.of(page - 1, pageSize)
        val resultPage: Page<Team> = teamRepo.findAllByDormitoryNotNull(pageable)

        return resultPage.map {
            object {
                val name = it.name
                val leaderId = it.leader.studentId
                val leaderName = it.leader.name
                val introduction = it.introduction
                val membersId = it.members.map(Student::studentId)
                val membersName = it.members.map(Student::name)
                val dormitory = it.dormitory?.let { dorm ->
                    object {
                        val zoneId = dorm.zoneId
                        val buildingId = dorm.buildingId
                        val roomId = dorm.roomId
                    }
                }
            }
        }.toPageResult().asRestResponse()
    }

    @GetMapping("/student/list")
    fun viewStudentList(
        @ModelAttribute pageInfo: PageInfo,
    ): RestResponse<Any?> {
        val page = pageInfo.page
        val pageSize = pageInfo.pageSize
        if (page < 1 || pageSize < 1) {
            return RestResponse.fail(404, "Error: Page number should be positive and pageSize should be greater than 0")
        }

        val pageable: Pageable = PageRequest.of(page - 1, pageSize)
        val resultPage: Page<Student> = studentRepo.findAll(pageable)
        if (page > resultPage.totalPages) {
            return RestResponse.fail(404, "Error: Requested page number is too large")
        }
        val studentListDto = StudentListDto(
            total = studentRepo.count().toInt(),
            page = page,
            pageSize = pageSize,
            rows = resultPage.content,

            )
        return RestResponse.success(studentListDto, "Return student list page $page")
    }

    @PostMapping("/dormitory/upload")
    fun uploadDormitory(
        @RequestBody dormList: List<DormInfoDto>,
    ): RestResponse<Any?> {
        var cnt: Int
        for (dorm in dormList) {
            val roomExist =
                dormitoryRepo.existsByRoomIdAndZoneIdAndBuildingId(dorm.roomId, dorm.zoneId, dorm.buildingId)
            if (roomExist) {
                return RestResponse.fail(404, "The dormitory you upload already exist")
            }
        }
        for (dorm in dormList) {
            dormitoryRepo.newDormitory(dorm.roomId, dorm.zoneId, dorm.size, dorm.buildingId, dorm.gender, dorm.info)
        }
        return RestResponse.success(null, "upload dormitory successfully")

    }

    @PostMapping("/student/upload")
    fun uploadStudent(
        @RequestBody studentUploadDtoList: List<StudentUploadDto>
    ): RestResponse<Any?> {
        for (studentInfoDto in studentUploadDtoList) {
            val studentExist = studentRepo.existsByStudentId(studentInfoDto.studentId)
            if (studentExist) {
                return RestResponse.fail(404, "The studentId you upload already exist")
            }
        }
        for (studentInfoDto in studentUploadDtoList) {
            val student = accountRepo.newStudent(
                studentInfoDto.studentId,
                studentInfoDto.name,
                studentInfoDto.password,
                studentInfoDto.gender
            )
            student.age = studentInfoDto.age
            student.department = studentInfoDto.department
            student.major = studentInfoDto.major

        }
        return RestResponse.success(null, "upload student successfully")

    }

    @PostMapping("/dormitory/edit")
    fun editDormitory(
        @RequestBody dormitoryDto: DormitoryDto
    ): RestResponse<Any?> {

        val isDormitory = dormitoryRepo.existsByRoomIdAndZoneIdAndBuildingId(
            dormitoryDto.roomId,
            dormitoryDto.zoneId,
            dormitoryDto.buildingId
        )
        if (!isDormitory) {
            return RestResponse.fail(404, "The dormitory you edit is not found in the database")

        }
        val dormitory = dormitoryRepo.findByRoomIdAndZoneIdAndBuildingId(
            dormitoryDto.roomId,
            dormitoryDto.zoneId,
            dormitoryDto.buildingId,
        ) ?: return RestResponse.fail(404, "No")

        dormitoryRepo.save(dormitory.apply {
            roomId = dormitoryDto.roomId
            zoneId = dormitoryDto.zoneId
            gender = dormitoryDto.gender
            size = dormitoryDto.size
            buildingId = dormitoryDto.buildingId
            info = dormitoryDto.info
        })


        return RestResponse.success(null, "Edit dormitory info Successfully")
    }

    @PostMapping("/dormitory/delete")
    fun deleteDormitory(
        @RequestBody dormDeleteDto: DormDeleteDto
    ): RestResponse<Any?> {
        val dormitory = dormitoryRepo.findById(dormDeleteDto.id)
            .getOrElse { return RestResponse.fail(404, "Dormitory not found in the database") }
        dormitoryRepo.delete(dormitory)
        return RestResponse.success(null, "Successfully delete dormitory $dormDeleteDto.id")
    }

    //
    @PostMapping("/student/edit")
    fun editStudentInfo(
        @RequestBody studentInfoDto: StudentInfoDto
    ): RestResponse<Any?> {
        val student = studentRepo.findByStudentId(studentInfoDto.studentId)
            ?: return RestResponse.fail(404, "Can not find the student in the database")

        student.apply {
            bedTime = studentInfoDto.bedTime ?: ""
            age = studentInfoDto.age ?: 0
            qq = studentInfoDto.qq ?: ""
            email = studentInfoDto.email ?: ""
            department = studentInfoDto.department ?: ""
            major = studentInfoDto.major ?: ""
            wechat = studentInfoDto.wechat ?: ""
            wakeUpTime = studentInfoDto.wakeUpTime ?: ""
            telephone = studentInfoDto.telephone ?: ""
            hobbies.clear()
            hobbies.addAll(studentInfoDto.hobbies)

            if (password.isNotBlank()) {
                password = studentInfoDto.password
            }
        }
        studentRepo.save(student)
        return RestResponse.success(null, "Edit student info Successfully")
    }

    @PostMapping("/student/delete")
    fun deleteStudent(
        @RequestBody integerWrapper: IntegerWrapper,
    ): RestResponse<Any?> {
        val student = studentRepo.findById(integerWrapper.id)
            .getOrElse { return RestResponse.fail(404, "fail to find student in the database") }
        studentRepo.delete(student)
        return RestResponse.success(null, "Successfully delete a student")
    }

    @GetMapping("/dormitory/list")
    fun viewDormitoryList(
        @ModelAttribute dormPageRequestDto: DormPageRequestDto
    ): RestResponse<Any?> {
        if (dormPageRequestDto.page < 1 || dormPageRequestDto.pageSize < 1) {
            return RestResponse.fail(404, "Error: Page number should be positive and pageSize should be greater than 0")
        }

        val pageable: Pageable = PageRequest.of(dormPageRequestDto.page - 1, dormPageRequestDto.pageSize)
        val resultPage: Page<Dormitory> = dormitoryRepo.findByZoneIdBuildingIdAndSizeWithZoneIdCheck(
            dormPageRequestDto.zoneId,
            dormPageRequestDto.buildingId,
            dormPageRequestDto.size,
            pageable,
        )
        if (dormPageRequestDto.page > resultPage.totalPages) {
            return RestResponse.fail(404, "Error: Requested page number is too large")
        }
        val rows = mutableListOf<Any>()
        for (dorm in resultPage.content) {
            val comments = commentRepo.findByDormitoryId(dorm.id!!)
            rows.add(object {
                val id = dorm.id
                val roomId = dorm.roomId
                val zoneId = dorm.zoneId
                val size = dorm.size
                val buildingId = dorm.buildingId
                val gender = dorm.gender
                val info = dorm.info
                val datetime = dorm.datetime
                val comments = comments
            })
        }
        val dormListDto = DormListDto(
            total = dormitoryRepo.count().toInt(),
            page = dormPageRequestDto.page,
            pageSize = dormPageRequestDto.pageSize,
            rows = rows,
        )
        return RestResponse.success(dormListDto, "Return dormitory list page ${dormPageRequestDto.page}")
    }


    @PostMapping("/select/time")
    fun editSelectionTime(
        @RequestBody dormiSelectTimeDto: DormiSelectTimeDto
    ): RestResponse<Any?> {

        val dormList = dormitoryRepo.findByZoneIdAndGender(dormiSelectTimeDto.zoneId, dormiSelectTimeDto.gender)
        for (dormitory in dormList) {
            dormitory.apply { this.datetime = dormiSelectTimeDto.datetime }
            dormitoryRepo.save(dormitory)
        }
        return RestResponse.success(null)
    }

    @GetMapping("/student/information/form")
    fun viewStudentInfo(
        @RequestParam id: Int
    ): RestResponse<Any?> {
        val student = studentRepo.findById(id)
            .getOrElse { return RestResponse.fail(404, "Id not exist in the database") }
        val studentInfoDto = StudentInfoDto(
            student.id!!,
            student.studentId,
            student.name,
            student.password,
            student.gender,
            student.bedTime,
            student.wakeUpTime,
            student.email,
            student.telephone,
            student.department,
            student.major,
            student.qq,
            student.wechat,
            student.age,
            student.hobbies,
        )
        return RestResponse.success(studentInfoDto, "check for student $id 's information")
    }

    @PostMapping("/team/remove_member")
    fun removeTeamMember(
        @RequestBody body: TeamMemberRemoveDto
    ): RestResponse<Any?> {
        val (teamId, studentId) = body
        val team = teamRepo.findById(teamId).getOrElse { return RestResponse.fail(404, "Team $teamId does not exist") }
        val teamFind = teamRepo.findTeamStudentBelongTo(studentId)
            ?: return RestResponse.fail(404, "Student $studentId does not join any team!")
        if (teamFind.id == team.id) {
            team.members.removeIf { student -> student.studentId == studentId }
            teamRepo.save(team)
            return RestResponse.success(null, "Delete successfully")
        } else {
            return RestResponse.fail(404, "Student $studentId does not belong to ${team.name}")
        }
    }

    @PostMapping("/announcement/publish")
    fun publishAnnouncement(
        @CurrentAccount account: Account,
        @RequestBody body: AnnouncementPublishDto
    ): RestResponse<Any?> {
        announcementRepo.newAnnouncement(account, body.receiver, body.priority, body.announcement)
        when(body.receiver){
            Announcement.Receiver.TEACHER -> {
                val teacherList = teacherRepo.findAll()
                for(teacher in teacherList){
                    notificationRepo.save(NewAnnouncementNotification(teacher))
                }

            }
            Announcement.Receiver.STUDENT -> {
                val studentList = studentRepo.findAll()
                for(student in studentList){
                    notificationRepo.save(NewAnnouncementNotification(student))
                }
            }
            Announcement.Receiver.TEACHER_AND_STUDENT -> {
                val studentList = studentRepo.findAll()
                for(student in studentList){
                    notificationRepo.save(NewAnnouncementNotification(student))
                }
                val teacherList = teacherRepo.findAll()
                for(teacher in teacherList){
                    notificationRepo.save(NewAnnouncementNotification(teacher))
                }
            }
        }
        return RestResponse.success(null, "Announcement published")
    }

    @GetMapping("/announcement/list")
    fun viewAnnouncement(
    ): RestResponse<Any?> {
        val list1 = announcementRepo.findByReceiver(Announcement.Receiver.TEACHER)
        val list2 = announcementRepo.findByReceiver(Announcement.Receiver.TEACHER_AND_STUDENT)
        return object {val rows = (list2+list1).toList()}.asRestResponse()
    }


}