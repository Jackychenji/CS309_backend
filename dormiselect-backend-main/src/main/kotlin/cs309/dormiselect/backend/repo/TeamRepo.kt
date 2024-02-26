package cs309.dormiselect.backend.repo

import cs309.dormiselect.backend.domain.Team
import cs309.dormiselect.backend.domain.account.Student
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface TeamRepo : CrudRepository<Team, Int> {
    fun findByMembersIdContaining(memberId: Int): List<Team>

    fun findByMembersStudentIdContaining(memberStudentId: Int): List<Team>

    fun findByDormitoryId(dormitoryId: Int): List<Team>

    fun findByFavoritesIdContaining(dormitoryId: Int): List<Team>

    //Size does not actually exist in Team database, because it is a getter!
    //fun findBySize(size: Int): List<Team>

    fun findByLeaderId(leaderId: Int): List<Team>

    fun existsByLeaderId(leaderId: Int): Boolean

    //no need to implement pre-declared function findAll(): List<Team>
    fun findAll(pageable: Pageable): Page<Team>

    fun findAllByDormitoryNotNull(pageable: Pageable): Page<Team>

    @Query("SELECT t FROM Team t JOIN t.members m WHERE m.studentId = :studentId")
    fun findStudentBelong(studentId: Int): Team?

    fun existsByDormitoryId(dormitoryId: Int): Boolean
}

fun TeamRepo.newTeam(leader: Student, name: String = "${leader.name}'s Team") = Team(leader, name).also { save(it) }

fun TeamRepo.findTeamStudentBelongTo(studentId: Int) = findAll().find { it.members.any { it.id == studentId } }

fun TeamRepo.findTeamDormitoryIsFavourite(dormitoryId: Int) = findByFavoritesIdContaining(dormitoryId)

fun TeamRepo.findDormitorySelectedBy(dormitoryId: Int) = findByDormitoryId(dormitoryId).firstOrNull()
fun TeamRepo.findTeamStudentBelongTo(student: Student) = findTeamStudentBelongTo(student.id!!)

fun TeamRepo.findTeamStudentLeads(studentId: Int) = findByLeaderId(studentId).firstOrNull()

fun TeamRepo.findTeamStudentLeads(student: Student) = findTeamStudentLeads(student.id!!)

fun TeamRepo.findByState(state: Team.State): List<Team> = findAll().filter { it.state == state }

// TODO findLeaderByMemembersContaining