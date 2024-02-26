package cs309.dormiselect.backend.domain.account

import jakarta.persistence.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder


@Entity
@Inheritance(strategy = InheritanceType.JOINED)
abstract class Account(
    var name: String,
    password0: String,
) {
    @Id
    @GeneratedValue
    val id: Int? = null

    var password: String = "$BCRYPT_PREFIX${encoder.encode(password0)}"
        set(value) {
            field = "$BCRYPT_PREFIX${encoder.encode(value)}"
        }

    fun checkPassword(passwordCompared: String) =
        encoder.matches(passwordCompared, password.removePrefix(BCRYPT_PREFIX))

    companion object {
        private val encoder = BCryptPasswordEncoder()
        private const val BCRYPT_PREFIX = "{bcrypt}"
    }
}