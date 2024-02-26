package cs309.dormiselect.backend.domain

import cs309.dormiselect.backend.domain.account.Student
import jakarta.persistence.*
import java.sql.Timestamp

@Entity
class TeamJoinRequest(
    @ManyToOne(optional = false) val student: Student,
    @ManyToOne(optional = false) val team: Team,
    val info: String,
) {
    @Id
    @GeneratedValue
    val id: Int? = null
    val datetime: Timestamp = Timestamp(System.currentTimeMillis())
    var state: State = State.WAITING

    fun accept() {
        if (state != State.WAITING) {
            return
        }

        team.members += student
        state = State.ACCEPT
    }

    fun decline() {
        if (state != State.WAITING) {
            return
        }

        state = State.DECLINE
    }

    fun cancel() {
        if (state != State.WAITING) {
            return
        }

        state = State.CANCELLED
        println(state)
    }

    enum class State {
        ACCEPT, DECLINE, WAITING, CANCELLED
    }
}