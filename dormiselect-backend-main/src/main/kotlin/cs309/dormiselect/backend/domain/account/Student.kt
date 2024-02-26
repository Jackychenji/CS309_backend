package cs309.dormiselect.backend.domain.account

import jakarta.persistence.Entity

@Entity
class Student(
    val studentId: Int,
    name: String,
    password: String,
    var gender: Gender,
) : Account(name, password) {

    enum class Gender {
        MALE, FEMALE
        // PREFER_NOT_TO_SAY can be added into the enum class but since gender-mixed accommodation is restricted, we cancel this gender
    }

    var bedTime: String = ""
    var wakeUpTime: String = ""
    val hobbies: MutableSet<String> = mutableSetOf()
    var email: String = ""
    var telephone: String = ""
    var department: String = ""
    var major: String = ""
    var qq: String = ""
    var wechat: String = ""
    var age: Int = 0
}