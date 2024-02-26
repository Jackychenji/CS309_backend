package cs309.dormiselect.backend.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne


@Entity
class DormitoryExchangeRequest(
    @ManyToOne(optional = false) val team1: Team,
    @ManyToOne(optional = false) val team2: Team,
) {
    @Id
    @GeneratedValue
    val id: Int? = null
    val state: State
        get() {
            if (approved1 == false || approved2 == false) {
                return State.REJECTED
            }
            if (approved1 == null || approved2 == null) {
                return State.WAITING
            }
            return State.APPROVED
        }

    var approved1: Boolean? = null
        protected set

    var approved2: Boolean? = null
        protected set

    var approvedTeacher: Boolean? = null

    fun approve1(approve: Boolean) {
        approved1 = approve
        checkApprove()
    }

    fun approve2(approve: Boolean) {
        approved2 = approve
        checkApprove()
    }

    fun approveTeacher(approve: Boolean) {
        approvedTeacher = approve
        checkApprove()
    }

    @JsonIgnore
    private var complete = false

    private fun checkApprove() {
        if (!complete && state == State.APPROVED) {
            team1.dormitory = team2.dormitory.also { team2.dormitory = team1.dormitory }
            complete = true
        }
    }

    /*
     Some requirements from the front end partners:

     - An exchange request happens between two teams who already get their dormitories in building A,B.
     - Remind an exchange requests need to be approved by BOTH teachers who are in charge of the A,B buildings where these two teams live.
     - A building may have many teachers in charge. The team who live in building A only need approval from one of them in A and one of teachers in charge of B
     */


    enum class State {
        APPROVED, REJECTED, WAITING
    }
}