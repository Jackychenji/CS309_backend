package cs309.dormiselect.backend.data.student

import cs309.dormiselect.backend.domain.account.Student

data class StudentInfoDto(
    val id: Int,
    val studentId: Int,
    val name: String,
    val password: String = "",
    val gender: Student.Gender,
    var bedTime: String?,
    var wakeUpTime: String?,
    var email: String?,
    var telephone: String?,
    var department: String?,
    var major: String?,
    var qq: String?,
    var wechat: String?,
    var age: Int?,
    var hobbies: MutableSet<String>
)