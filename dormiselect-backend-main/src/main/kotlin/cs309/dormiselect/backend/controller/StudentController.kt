package cs309.dormiselect.backend.controller

import cs309.dormiselect.backend.config.CurrentAccount
import cs309.dormiselect.backend.data.PageInfo
import cs309.dormiselect.backend.data.RestResponse
import cs309.dormiselect.backend.data.asRestResponse
import cs309.dormiselect.backend.data.dormitory.*
import cs309.dormiselect.backend.data.student.StudentInfoDto
import cs309.dormiselect.backend.data.student.StudentListDto
import cs309.dormiselect.backend.domain.Announcement
import cs309.dormiselect.backend.domain.Dormitory
import cs309.dormiselect.backend.domain.account.Account
import cs309.dormiselect.backend.domain.account.Student
import cs309.dormiselect.backend.repo.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*
import kotlin.jvm.optionals.getOrElse

@RestController
@RequestMapping("/api/student")
class StudentController(
    private val teamRepo: TeamRepo,
    private val dormitoryRepo: DormitoryRepo,
    private val commentRepo: CommentRepo,
    private val studentRepo: StudentRepo,
    private val announcementRepo: AnnouncementRepo,
    private val replyRepo: ReplyRepo,
    private val accountRepo: AccountRepo,
) {

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
        for(dorm in resultPage.content){
            val comments = commentRepo.findByDormitoryId(dorm.id!!)
            val commentList = mutableListOf<Any>()
            for(comment in comments){
                val replies = replyRepo.findByCommentId(comment.id!!)
                commentList.add(object {
                    val id = comment.id
                    val author = comment.author
                    val content = comment.content
                    val likeNum = comment.likeNum
                    val replies = replies
                })

            }
            rows.add(object {
                val id = dorm.id
                val roomId = dorm.roomId
                val zoneId = dorm.zoneId
                val size = dorm.size
                val buildingId = dorm.buildingId
                val gender = dorm.gender
                val info = dorm.info
                val datetime = dorm.datetime
                val comments = commentList
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


    @GetMapping("/announcement/list")
    fun viewAnnouncement(
    ): RestResponse<Any?> {
        val list1 = announcementRepo.findByReceiver(Announcement.Receiver.STUDENT)
        val list2 = announcementRepo.findByReceiver(Announcement.Receiver.TEACHER_AND_STUDENT)
        return object {val rows = (list2+list1).toList()}.asRestResponse()
    }


    @GetMapping("/information")
    fun viewStudentInfo(@CurrentAccount account: Account):RestResponse<Any?>{
        account.id?:return RestResponse.fail(404,"You haven't login yet")
        val student = studentRepo.findById(account.id!!)
            .getOrElse { return RestResponse.fail(404, "This id is not in the database")}
        return student.asRestResponse("The student information is found!")
    }

    @PostMapping("/information")
    fun editStudentInfo(
        @CurrentAccount account: Account,
        @ModelAttribute studentInfoDto: StudentInfoDto,
    ):RestResponse<Any?>{
        account.id?:return RestResponse.fail(0,"You have not login yet")
        if(account.id!=studentInfoDto.id){
            return RestResponse.fail(401,"Your login account is different from the account you want to edit")
        }
        val student = studentRepo.findById(account.id!!)
            .getOrElse {return RestResponse.fail(404,"The id is not in the database") }
        student.apply{
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
            hobbies += studentInfoDto.hobbies
        }
        studentRepo.save(student)
        return RestResponse.success(null,"Successfully edit!")

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

    @PostMapping("/dormitory/comment")
    fun commentOnDorm(
        @RequestBody dormCommentDto: DormCommentDto,
        @CurrentAccount account: Account,
    ):RestResponse<Any?>{
        val dorm = dormitoryRepo.findById(dormCommentDto.id)
            .getOrElse { return RestResponse.fail(404,"Invalid dormitory ID") }

        commentRepo.newComment(dorm,account,dormCommentDto.content)
        return RestResponse.success(null,"Successfully post a comment!")
    }

    @PostMapping("/dormitory/reply")
    fun replyComment(
        @RequestBody reply: CommentReplyDto,
        @CurrentAccount account: Account,
    ): RestResponse<Any?>{
        val comment = commentRepo.findById(reply.id)
            .getOrElse { return RestResponse.fail(404,"Invalid comment ID") }

        val receiver = accountRepo.findById(reply.receiver)
            .getOrElse { return RestResponse.fail(404,"receiver not found in database") }
        replyRepo.newReply(comment,account,receiver,reply.content)
        return RestResponse.success(null,"Successfully post a reply!")
    }

    @GetMapping("/dormitory/comment/list")
    fun fetchComment(
        @RequestParam id: Int
    ):RestResponse<Any?> {
        val isDorm = dormitoryRepo.existsById(id)
        if (isDorm) {
            val comments = commentRepo.findByDormitoryId(id)
            return RestResponse.success(comments, "Fetch the comment list of dormitory $id")
        }
        else{
            return RestResponse.fail(404,"Invalid dormitory ID")
        }
    }
    @GetMapping("/dormitory/reply/list")
    fun fetchReplies(
        @RequestParam id: Int,
    ):RestResponse<Any?>{
        val isComment = commentRepo.existsById(id)
        if(isComment){
            val replies = replyRepo.findByCommentId(id)
            return RestResponse.success(replies,"Fetch the reply list")
        }
        else{
            return RestResponse.fail(404,"Invalid comment Id")
        }

    }

    @PostMapping("/dormitory/comment/like")
    fun likeComment(
        @RequestBody  commentLikeDto: CommentLikeDto
    ):RestResponse<Any?>{
        val comment = commentRepo.findById(commentLikeDto.id)
            .getOrElse { return RestResponse.fail(404,"The comment does not exist") }
        if(commentLikeDto.hasLiked){
            comment.dislike()
        }else{
            comment.like()
        }
        commentRepo.save(comment)
        return RestResponse.success(object {val likeNum = comment.likeNum},"you just liked comment")
    }

    @PostMapping("/dormitory/reply/like")
    fun likeReply(
        @RequestBody commentLikeDto: CommentLikeDto
    ):RestResponse<Any?>{
        val comment = replyRepo.findById(commentLikeDto.id)
            .getOrElse { return RestResponse.fail(404,"The comment does not exist") }
        if(commentLikeDto.hasLiked){
            comment.dislike()
        }else{
            comment.like()
        }
        replyRepo.save(comment)
        return RestResponse.success(object {val likeNum = comment.likeNum},"you just liked reply")
    }


}





