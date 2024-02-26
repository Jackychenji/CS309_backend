package cs309.dormiselect.backend.repo

import cs309.dormiselect.backend.domain.notification.Notification
import org.springframework.data.repository.CrudRepository

interface NotificationRepo : CrudRepository<Notification, Long> {
    fun findAllValidByReceiverIdAndIdIn(receiverId: Int, ids: Iterable<Long>): List<Notification>

    fun findAllValidAndNotReadByReceiverId(receiverId: Int): List<Notification>
    fun findAllValidAndNotDoneByReceiverId(receiverId: Int): List<Notification>
    fun findAllValidByReceiverId(receiverId: Int): List<Notification>

    //TODO: check if any operation needs to insert a notification and create subclasses for them
}
