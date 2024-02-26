package cs309.dormiselect.backend.data.dormitory

import javax.sound.midi.MidiDeviceReceiver

data class CommentReplyDto(
    val id: Int, //increment id in Comment table
    val receiver: Int,
    val content: String,
)
