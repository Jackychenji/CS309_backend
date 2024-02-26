package cs309.dormiselect.backend.domain.account

import jakarta.persistence.Entity

@Entity
class Administrator(name: String, password: String) : Account(name, password) {
    val administratorId: Int? = null
}