package cs309.dormiselect.backend.repo

import cs309.dormiselect.backend.domain.Comment
import cs309.dormiselect.backend.domain.Dormitory
import cs309.dormiselect.backend.domain.account.Account
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.sql.Timestamp

interface CommentRepo: CrudRepository<Comment, Int> {
    fun findByDormitoryId(dormitoryId: Dormitory): List<Comment>

    fun findByAuthor(account: Account): List<Comment>
    fun findByPostTime(postTimes: Timestamp): List<Comment>

    fun findByDormitoryId(dormitoryId: Int): List<Comment>

}
fun CommentRepo.newComment(dormitory: Dormitory,author: Account, content:String): Comment {
    return Comment(dormitory, author, content).also { save(it) }
}