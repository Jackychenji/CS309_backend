package cs309.dormiselect.backend.repo

import cs309.dormiselect.backend.domain.Announcement
import cs309.dormiselect.backend.domain.account.Account
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository

interface AnnouncementRepo : CrudRepository<Announcement, Int> {
    fun findAll(pageable: Pageable): Page<Announcement>
    fun findByReceiver(receiver: Announcement.Receiver): List<Announcement>
}

fun AnnouncementRepo.newAnnouncement(
    author: Account,
    receiver: Announcement.Receiver,
    priority: Announcement.Priority,
    content: String
) = Announcement(author, content, receiver, priority).also { save(it) }