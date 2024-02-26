package cs309.dormiselect.backend.repo

import cs309.dormiselect.backend.domain.Team
import cs309.dormiselect.backend.domain.TeamMessage
import cs309.dormiselect.backend.domain.account.Account
import org.springframework.data.repository.CrudRepository

interface TeamMessageRepo : CrudRepository<TeamMessage, Int> {
    fun findAllByReceiver(receiver: Team): List<TeamMessage>
    fun findAllByReceiverId(receiverId: Int): List<TeamMessage>
    fun findAllNotHiddenByReceiver(receiver: Team): List<TeamMessage>
    fun findAllNotHiddenByReceiverId(receiverId: Int): List<TeamMessage>
}

fun TeamMessageRepo.newMessage(sender: Account, receiver: Team, message: String) =
    TeamMessage(sender, receiver, message).also { save(it) }