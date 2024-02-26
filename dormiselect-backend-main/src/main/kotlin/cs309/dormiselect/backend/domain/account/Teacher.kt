package cs309.dormiselect.backend.domain.account

import jakarta.persistence.Entity

@Entity
class Teacher(
    name: String,
    password: String,
    val teacherId:Int
) : Account(name, password) {
    var buildingInCharge: Int? = null
}