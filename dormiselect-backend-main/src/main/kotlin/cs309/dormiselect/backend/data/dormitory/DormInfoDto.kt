package cs309.dormiselect.backend.data.dormitory

import cs309.dormiselect.backend.domain.account.Student

data class DormInfoDto(
    val roomId: Int,
    val zoneId: Int,
    val size: Int,
    val gender: Student.Gender,
    val buildingId: Int,
    val info: String,
)
