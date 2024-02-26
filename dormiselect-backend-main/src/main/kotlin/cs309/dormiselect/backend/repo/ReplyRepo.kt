package cs309.dormiselect.backend.repo

import cs309.dormiselect.backend.domain.Comment
import cs309.dormiselect.backend.domain.Reply
import cs309.dormiselect.backend.domain.account.Account
import org.springframework.data.repository.CrudRepository
import javax.sound.midi.Receiver

interface ReplyRepo : CrudRepository<Reply, Int>{

    fun findByCommentId(commentId: Int): List<Reply>

}
fun ReplyRepo.newReply(comment: Comment, sender: Account, receiver: Account, content: String): Reply {
    return Reply(comment, sender, receiver, content).also { save(it) }
}