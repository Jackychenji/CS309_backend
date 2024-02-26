package cs309.dormiselect.backend.repo

import cs309.dormiselect.backend.domain.account.Administrator
import cs309.dormiselect.backend.domain.account.Student
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.sql.Timestamp
import kotlin.test.assertEquals

@DataJpaTest
@AutoConfigureTestDatabase
class AccountRepoTest(
    @Autowired val accountRepo: AccountRepo,
) {
    @Test
    fun testInsert() {
        val student = accountRepo.newStudent(114514, "student", "114514", Student.Gender.MALE)
        student.hobbies += setOf("sing", "jump", "rap", "basketball")
        student.bedTime = Timestamp.valueOf("2021-01-01 23:00:00").toString()
        student.wakeUpTime = Timestamp.valueOf("2021-01-02 07:00:00").toString()
        val admin = accountRepo.newAdministrator("admin", "1919810")

        val savedStudent = accountRepo.findByName("student")
        val savedAdmin = accountRepo.findByName("admin")

        assert(savedStudent != null && savedStudent is Student)
        assert(savedAdmin != null && savedAdmin is Administrator)

        with(savedStudent as Student) {
            println("I'm a student who likes ${hobbies.joinToString()}, sleeps at $bedTime and wakes up at $wakeUpTime")
            println("My name is $name and my password is $password")
        }

        with(savedAdmin as Administrator) {
            println("I'm an admin.")
            println("My name is $name and my password is $password")
        }

        savedStudent.hobbies += "music"

        assertEquals(savedStudent, accountRepo.findByName("student"))
        assertEquals(savedStudent.hobbies, (accountRepo.findByName("student") as? Student)?.hobbies)
    }
}