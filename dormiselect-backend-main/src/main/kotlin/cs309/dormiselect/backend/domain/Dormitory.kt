package cs309.dormiselect.backend.domain

import cs309.dormiselect.backend.domain.account.Student
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import java.sql.Timestamp

@Entity
class Dormitory(
    var roomId: Int,
    var zoneId: Int,
    var size: Int,
    var buildingId: Int,
    var gender: Student.Gender,
    var info: String = "",
) {
    @Id
    @GeneratedValue
    val id: Int? = null

    // @OneToMany(orphanRemoval = true)
    // val comments: MutableList<Comment> = mutableListOf()
    var datetime: Timestamp? = null
}