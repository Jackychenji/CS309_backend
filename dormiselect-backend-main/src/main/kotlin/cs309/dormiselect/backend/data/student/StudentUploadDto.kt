package cs309.dormiselect.backend.data.student

import cs309.dormiselect.backend.domain.account.Student

data class StudentUploadDto(
    val studentId: Int,
    val name: String,
    val password: String,
    val gender: Student.Gender,
    var department: String = "",
    var major: String = "",
    var age: Int = 0,
)
