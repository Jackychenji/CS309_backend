package cs309.dormiselect.backend.data.message

data class MessageQueryDto(
    val timeFrom: Long? = null,
    val timeTo: Long? = null,
    val receiverId: Int? = null
)
