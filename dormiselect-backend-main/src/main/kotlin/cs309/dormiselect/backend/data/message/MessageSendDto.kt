package cs309.dormiselect.backend.data.message

data class MessageSendDto(
    val receiverId: Int? = null,
    val message: String,
)
