package cs309.dormiselect.backend.repo

import cs309.dormiselect.backend.domain.account.Student
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import kotlin.test.Test
import kotlin.test.assertEquals

@DataJpaTest
@AutoConfigureTestDatabase
class TeamMessageRepoTest(
    @Autowired val accountRepo: AccountRepo,
    @Autowired val teamRepo: TeamRepo,
    @Autowired val teamMessageRepo: TeamMessageRepo,
) {
    @Test
    fun testInsert() {
        val account1 = accountRepo.newStudent(1, "1", "114", Student.Gender.MALE)
        val account2 = accountRepo.newStudent(2, "2", "514", Student.Gender.MALE)
        val team1 = teamRepo.newTeam(account1)
        team1.members += account2

        val account3 = accountRepo.newStudent(3, "3", "1919", Student.Gender.FEMALE)
        val account4 = accountRepo.newStudent(4, "4", "810", Student.Gender.FEMALE)
        val team2 = teamRepo.newTeam(account3)
        team2.members += account4

        val message1 = teamMessageRepo.newMessage(account1, team1, "Hello, team1!")
        val message2 = teamMessageRepo.newMessage(account2, team1, "Hello, team1!")
        val message3 = teamMessageRepo.newMessage(account3, team2, "Hello, team2!")
        val message4 = teamMessageRepo.newMessage(account4, team2, "Hello, team2!")

        assertEquals(listOf(message1, message2), teamMessageRepo.findAllByReceiver(team1))
        assertEquals(listOf(message1, message2), teamMessageRepo.findAllByReceiverId(team1.id!!))
        assertEquals(listOf(message3, message4), teamMessageRepo.findAllByReceiver(team2))
        assertEquals(listOf(message3, message4), teamMessageRepo.findAllByReceiverId(team2.id!!))
    }
}