package cs309.dormiselect.backend.data.dormitory

import cs309.dormiselect.backend.domain.account.Student
import java.sql.Timestamp

data class DormiSelectTimeDto(
    val zoneId: Int,
    val gender: Student.Gender,
    val datetime: Timestamp,
)
