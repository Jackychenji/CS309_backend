package cs309.dormiselect.backend.controller

import cs309.dormiselect.backend.config.CurrentAccount
import cs309.dormiselect.backend.data.*
import cs309.dormiselect.backend.data.message.MessageQueryDto
import cs309.dormiselect.backend.data.message.MessageSendDto
import cs309.dormiselect.backend.domain.PrivateMessage
import cs309.dormiselect.backend.domain.account.Account
import cs309.dormiselect.backend.domain.account.Administrator
import cs309.dormiselect.backend.domain.notification.Notification
import cs309.dormiselect.backend.repo.*
import org.apache.commons.logging.LogFactory
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.sql.Timestamp
import java.util.*
import kotlin.jvm.optionals.getOrElse

@RestController
@RequestMapping("/api/account")
class AccountController(
    val accountRepo: AccountRepo,
    val privateMessageRepo: PrivateMessageRepo,
    val notificationRepo: NotificationRepo,
) {
    private val logger = LogFactory.getLog(javaClass)

    private fun tryInitRootAccount() {
        if (accountRepo.findAll().any { it is Administrator }) {
            return
        }

        val password = if (System.getenv("DEBUG") == null) {
            UUID.randomUUID().toString()
        } else {
            "114514"
        }

        val account = Administrator("root", password)
        accountRepo.save(account)

        logger.info("No admin account is found so a new one is created.")
        logger.info("Username: ${account.name}, Password: $password")
    }

    init {
        tryInitRootAccount()
    }

    @GetMapping("/hello")
    fun hello(@CurrentAccount account: Account) = RestResponse.success("Hello, ${account.name}!")

    @GetMapping("/error")
    fun error(): Nothing {
        throw ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, "I'm a teapot.")
    }

    @PostMapping("/change_password")
    fun changePassword(@CurrentAccount account: Account, @RequestBody body: PasswordChangeDto): RestResponse<Nothing?> {
        require(account.checkPassword(body.old)) { "Old password is incorrect." }
        account.password = body.new
        return RestResponse.success(null, "Password changed successfully.")
    }

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequestDto): RestResponse<Any?> {

        if (accountRepo.findByName(request.username) != null) {
            return RestResponse.fail(404, "This username already exist!")
        }
        val account = when (request.type) {
            "Student" -> accountRepo.newStudent(request.id, request.username, request.password, request.gender)
            //TODO: handling logic of Teacher and Administrator account
            else -> {
                return RestResponse.fail(404, "Invalid account type!")
            }
        }


        return RestResponse.success("Account register successfully")
    }

    @PostMapping("/message/query")
    fun getMessage(
        @CurrentAccount account: Account,
        @RequestBody body: MessageQueryDto
    ): RestResponse<List<PrivateMessage>?> {
        val result = if (body.receiverId != null) {
            privateMessageRepo.findAllBySenderAndReceiverId(account, body.receiverId)
        } else {
            privateMessageRepo.findAllBySenderOrReceiver(account, account)
        }

        return result.filter {
            it.timestamp >= Timestamp(body.timeFrom ?: 0) && (it.timestamp <= Timestamp(body.timeTo ?: Long.MAX_VALUE))
        }.asRestResponse()
    }

    @PostMapping("/message/send")
    fun sendMessage(@CurrentAccount account: Account, @RequestBody body: MessageSendDto): RestResponse<Any?> {
        val receiver =
            accountRepo.findById(body.receiverId ?: throw IllegalArgumentException("receiver id is required"))
                .getOrElse { throw IllegalArgumentException("receiver not found") }

        privateMessageRepo.newMessage(account, receiver, body.message)
        return RestResponse.success(null, "message sent")
    }

    @GetMapping("/notification/all")
    fun getAllNotification(@CurrentAccount account: Account): RestResponse<PageResult<Notification>?> {
        val pageable = PageRequest.of(0, Int.MAX_VALUE)
        return notificationRepo.findAllValidByReceiverId(account.id!!).toPageResult(pageable).asRestResponse()
    }

    @GetMapping("/notification/unread")
    fun getUnreadNotification(@CurrentAccount account: Account): RestResponse<PageResult<Notification>?> {
        val pageable = PageRequest.of(0, Int.MAX_VALUE)
        return notificationRepo.findAllValidAndNotReadByReceiverId(account.id!!).toPageResult(pageable).asRestResponse()
    }

    @GetMapping("/notification/undone")
    fun getUndoneNotification(@CurrentAccount account: Account): RestResponse<List<Notification>?> {
        return notificationRepo.findAllValidAndNotDoneByReceiverId(account.id!!).asRestResponse()
    }

    @PostMapping("/notification/read")
    fun markNotificationAsRead(@CurrentAccount account: Account, @RequestBody body: List<Long>?): RestResponse<Any?> {
        val notifications = if (body == null) {
            notificationRepo.findAllValidAndNotReadByReceiverId(account.id!!)
        } else {
            notificationRepo.findAllValidByReceiverIdAndIdIn(account.id!!, body)
        }

        notifications.forEach {
            it.read = true
        }

        return RestResponse.success(null, "notification marked as read")
    }

    @PostMapping("/notification/done")
    fun markNotificationAsDone(@CurrentAccount account: Account, @RequestBody body: List<Long>?): RestResponse<Any?> {
        val notifications = if (body == null) {
            notificationRepo.findAllValidAndNotDoneByReceiverId(account.id!!)
        } else {
            notificationRepo.findAllValidByReceiverIdAndIdIn(account.id!!, body)
        }

        notifications.forEach {
            it.done = true
        }

        return RestResponse.success(null, "notification marked as done")
    }

    @GetMapping("/userInfo")
    fun userInfo(@CurrentAccount account: Account): RestResponse<Any?> {
        return account.asRestResponse("This is user information")
    }

}