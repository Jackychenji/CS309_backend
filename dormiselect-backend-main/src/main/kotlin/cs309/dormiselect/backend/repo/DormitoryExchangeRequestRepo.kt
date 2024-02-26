package cs309.dormiselect.backend.repo

import cs309.dormiselect.backend.domain.DormitoryExchangeRequest
import cs309.dormiselect.backend.domain.Team
import org.springframework.data.repository.CrudRepository

interface DormitoryExchangeRequestRepo : CrudRepository<DormitoryExchangeRequest, Int> {
    fun findByTeam1OrTeam2(team1: Team, team2: Team): List<DormitoryExchangeRequest>
}

fun DormitoryExchangeRequestRepo.findByTeam(team: Team) = findByTeam1OrTeam2(team, team)