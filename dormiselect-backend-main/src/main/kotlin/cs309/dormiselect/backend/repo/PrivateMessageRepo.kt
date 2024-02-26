package cs309.dormiselect.backend.repo

import cs309.dormiselect.backend.domain.PrivateMessage
import cs309.dormiselect.backend.domain.account.Account
import org.springframework.data.repository.CrudRepository

interface PrivateMessageRepo : CrudRepository<PrivateMessage, Int> {
    fun findAllBySenderOrReceiver(sender: Account, receiver: Account): List<PrivateMessage>

    fun findAllByReceiverId(receiverId: Int): List<PrivateMessage>

    fun findAllBySenderAndReceiverId(sender: Account, receiverId: Int): List<PrivateMessage>
}

fun PrivateMessageRepo.newMessage(sender: Account, receiver: Account, message: String) =
    PrivateMessage(sender, receiver, message).also { save(it) }