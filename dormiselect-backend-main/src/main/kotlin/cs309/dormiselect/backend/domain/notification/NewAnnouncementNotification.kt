package cs309.dormiselect.backend.domain.notification

import cs309.dormiselect.backend.domain.Announcement
import cs309.dormiselect.backend.domain.account.Account
import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne

@Entity
class NewAnnouncementNotification(
    @ManyToOne(optional = false) val account: Account,
) : Notification(account,"你有一条新消息！")


