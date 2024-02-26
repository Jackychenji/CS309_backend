package cs309.dormiselect.backend.domain.notification

import cs309.dormiselect.backend.domain.account.Account
import jakarta.persistence.*
import java.sql.Time
import java.sql.Timestamp

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
class Notification(
    @ManyToOne val receiver: Account,
    val message: String,
) {
    @Id
    @GeneratedValue
    val id: Long? = null
    // valid: to hide or not
    // read:
    var valid = true
    var read = false
    var time: Timestamp = Timestamp(System.currentTimeMillis())
    var done = false
        set(value) {
            if (value) {
                read = true
            }
            field = value
        }
}