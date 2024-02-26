package cs309.dormiselect.backend.domain.notification

import cs309.dormiselect.backend.domain.Team
import cs309.dormiselect.backend.domain.TeamJoinRequest
import cs309.dormiselect.backend.domain.account.Student
import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne

@Entity
class TeamJoinRequestDealtNotification(
    @ManyToOne(optional = false) val team: Team,
    @ManyToOne(optional = false) val student: Student,
    val state: TeamJoinRequest.State
) : Notification(student, run {
    val t = when (state) {
        TeamJoinRequest.State.ACCEPT -> "同意"
        TeamJoinRequest.State.DECLINE -> "拒绝"
        else -> return@run ""
    }
    "${team.leader.name} $t 了你加入 ${team.name} 的申请。"
})

fun TeamJoinRequest.generateDealtNotification(): TeamJoinRequestDealtNotification {
    return TeamJoinRequestDealtNotification(team, student, state)
}