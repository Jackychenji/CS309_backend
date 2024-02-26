package cs309.dormiselect.backend.repo

import cs309.dormiselect.backend.domain.Team
import cs309.dormiselect.backend.domain.TeamJoinRequest
import cs309.dormiselect.backend.domain.account.Student
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository

interface TeamJoinRequestRepo : CrudRepository<TeamJoinRequest, Int> {
    fun findAllByTeamId(teamId: Int): List<TeamJoinRequest>

    fun findAllByTeamId(teamId: Int, pageable: Pageable): Page<TeamJoinRequest>

    fun findAllByStudentId(studentId: Int, pageable: Pageable): Page<TeamJoinRequest>
    fun removeAllByStudentId(studentId: Int)
    fun findByTeamIdAndStudentId(teamId: Int, studentId: Int): TeamJoinRequest?
    fun findByTeamIdAndStudentIdAndState(teamId: Int, studentId: Int, state: TeamJoinRequest.State): TeamJoinRequest?
}

fun TeamJoinRequestRepo.newRequest(teamRepo: TeamRepo, team: Team, student: Student, info: String): TeamJoinRequest {
    if (teamRepo.findTeamStudentBelongTo(student) != null) {
        throw IllegalArgumentException("The student has already joined a team.")
    }

    if (findByTeamIdAndStudentIdAndState(team.id!!, student.id!!, TeamJoinRequest.State.WAITING) != null) {
        throw IllegalArgumentException("The student has already sent a request.")
    }

    return TeamJoinRequest(student, team, info).also { save(it) }
}

fun TeamJoinRequestRepo.acceptRequest(teamRepo: TeamRepo, requestId: Int) {
    val request = findById(requestId).orElseThrow { IllegalArgumentException("Request not found.") }
    if (request.state != TeamJoinRequest.State.WAITING) {
        throw IllegalArgumentException("The request is already be ${request.state}.")
    }

    if (teamRepo.findTeamStudentBelongTo(request.student) != null) {
        throw IllegalArgumentException("The student has already joined a team.")
    }

    if (request.team.state != Team.State.RECRUITING) {
        throw IllegalArgumentException("The team is not recruiting.")
    }

    request.accept()
    save(request)
}

fun TeamJoinRequestRepo.declineRequest(requestId: Int) {
    val request = findById(requestId).orElseThrow { IllegalArgumentException("Request not found.") }
    if (request.state != TeamJoinRequest.State.WAITING) {
        throw IllegalArgumentException("The request is already be ${request.state}.")
    }

    request.decline()
    save(request)
}

fun TeamJoinRequestRepo.cancelRequest(requestId: Int) {
    val request = findById(requestId).orElseThrow { IllegalArgumentException("Request not found.") }
    if (request.state != TeamJoinRequest.State.WAITING) {
        throw IllegalArgumentException("The request is already be ${request.state}.")
    }

    request.cancel()
    save(request)
}

