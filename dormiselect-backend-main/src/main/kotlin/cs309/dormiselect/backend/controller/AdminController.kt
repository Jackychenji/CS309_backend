package cs309.dormiselect.backend.controller

import cs309.dormiselect.backend.data.IntegerWrapper
import cs309.dormiselect.backend.data.PageInfo
import cs309.dormiselect.backend.data.PageResult
import cs309.dormiselect.backend.data.RestResponse
import cs309.dormiselect.backend.data.teacher.TeacherUploadDto
import cs309.dormiselect.backend.domain.account.Teacher
import cs309.dormiselect.backend.repo.AccountRepo
import cs309.dormiselect.backend.repo.TeacherRepo
import cs309.dormiselect.backend.repo.newTeacher
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.jvm.optionals.getOrElse

@RestController
@RequestMapping("api/administrator")
class AdminController(
    private val teacherRepo: TeacherRepo, private val accountRepo: AccountRepo,
) {
    @GetMapping("/teacher/list")
    fun viewTeacherList(
        @ModelAttribute pageInfo: PageInfo,
    ): RestResponse<Any?> {
        val pageable: Pageable = PageRequest.of(pageInfo.page - 1, pageInfo.pageSize)
        val resultPage: Page<Teacher> = teacherRepo.findAll(pageable)
        val page = PageResult<Teacher>(teacherRepo.count().toInt(),pageInfo.page,pageInfo.pageSize,resultPage.content)
        return RestResponse.success(page, "Return teacher list page ${pageInfo.pageSize}")
    }

    @PostMapping("/teacher/upload")
    fun uploadTeacher(
        @RequestBody teacherInfoList: List<TeacherUploadDto>
    ): RestResponse<Any?> {
        for (teacherInfo in teacherInfoList) {
            if (teacherRepo.existsByTeacherId(teacherInfo.teacherId)) {
                return RestResponse.fail(404, "The teacher you upload already exists")
            }
        }

        for (teacherInfo in teacherInfoList) {
            teacherRepo.newTeacher(teacherInfo.teacherId, teacherInfo.name, teacherInfo.password)
                .apply { this.buildingInCharge = teacherInfo.buildingInCharge}

        }
        return RestResponse.success(null, "Successfully upload teacher accounts")
    }

    @PostMapping("/teacher/edit")
    fun editTeacher(
        @RequestBody teacherInfo: Teacher,
    ):RestResponse<Any?>{
        val teacher = teacherRepo.findById(teacherInfo.id!!)
            .getOrElse { return RestResponse.fail(404,"No such teacher inside database") }

        teacher.apply {

            this.buildingInCharge = teacherInfo.buildingInCharge
            if (teacherInfo.password!=""){
                this.password = teacherInfo.password
            }
        }
        teacherRepo.save(teacher)
        accountRepo.save(teacher)
        return RestResponse.success(null,"Success")
    }

    @PostMapping("/teacher/delete")
    fun deleteTeacher(
        @RequestBody integerWrapper: IntegerWrapper,
    ):RestResponse<Any?>{
        val teacher = teacherRepo.findById(integerWrapper.id)
            .getOrElse { return RestResponse.fail(404,"This teacher is not inside the database") }
        teacherRepo.delete(teacher)
        return RestResponse.success(null)
    }
}