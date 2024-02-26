package cs309.dormiselect.backend.domain

import cs309.dormiselect.backend.domain.account.Account
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import java.sql.Timestamp

@Entity
class TeamMessage(
    @ManyToOne val sender: Account,
    @ManyToOne val receiver: Team,
    val message: String,
    val timestamp: Timestamp = Timestamp(System.currentTimeMillis()),
    val hidden: Boolean = false,
) {
    @Id
    @GeneratedValue
    val id: Int? = null
}