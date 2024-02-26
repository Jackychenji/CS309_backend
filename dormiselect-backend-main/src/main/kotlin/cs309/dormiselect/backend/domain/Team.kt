package cs309.dormiselect.backend.domain

import cs309.dormiselect.backend.domain.account.Student
import jakarta.persistence.*


@Entity
class Team(
    @OneToOne(optional = false) var leader: Student,
    var name: String,
) {
    @Id
    @GeneratedValue
    val id: Int? = null

    @OneToMany
    @Suppress("LeakingThis")
    val members: MutableList<Student> = mutableListOf(leader)

    @ManyToMany
    val favorites: MutableList<Dormitory> = mutableListOf()

    @OneToOne
    var dormitory: Dormitory? = null

    var recruiting = true
    var maxSize = 4
    var introduction: String? = null
    val state: State
        get() {
            if (!recruiting) {
                return State.NOT_RECRUITING
            }
            if (size < maxSize) {
                return State.RECRUITING
            }
            return State.FULL
        }

    val size get() = members.size

    val full get() = size == maxSize

    val gender get() = leader.gender

    enum class State {
        RECRUITING, NOT_RECRUITING, FULL
    }
}