package cs309.dormiselect.backend.data

import cs309.dormiselect.backend.domain.account.Student

data class RegisterRequestDto(
    val id: Int,
    val username: String,
    val email: String?,
    val password: String,
    val type: String,
    val gender: Student.Gender,
)
