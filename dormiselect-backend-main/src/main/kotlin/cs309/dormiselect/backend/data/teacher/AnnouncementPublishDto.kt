package cs309.dormiselect.backend.data.teacher

import cs309.dormiselect.backend.domain.Announcement

data class AnnouncementPublishDto(
    val receiver: Announcement.Receiver,
    val priority: Announcement.Priority,
    val announcement: String
)
