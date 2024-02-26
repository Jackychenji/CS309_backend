package cs309.dormiselect.backend.domain.notification

import cs309.dormiselect.backend.domain.Team
import cs309.dormiselect.backend.domain.TeamJoinRequest
import cs309.dormiselect.backend.domain.account.Student
import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne

@Entity
class NewTeamJoinRequestNotification(
    @ManyToOne(optional = false) val team: Team,
    @ManyToOne(optional = false) val student: Student,
) : Notification(team.leader, "${student.name} 申请加入你的队伍。")

fun TeamJoinRequest.generateNewApplyNotification(): NewTeamJoinRequestNotification {
    return NewTeamJoinRequestNotification(team, student)
}