package cs309.dormiselect.backend.repo

import cs309.dormiselect.backend.domain.account.Account
import cs309.dormiselect.backend.domain.account.Administrator
import cs309.dormiselect.backend.domain.account.Student
import cs309.dormiselect.backend.domain.account.Teacher
import org.springframework.data.repository.CrudRepository

interface AccountRepo : CrudRepository<Account, Int> {
    fun findByName(name: String): Account?

    //No! Don't! ID is Int, not String!
    //fun findById(id: String): Account?
    fun findByNameAndPassword(name: String, password: String): Account?
}

fun AccountRepo.newStudent(
    studentId: Int, name: String, password: String, gender: Student.Gender
): Student {
    return Student(studentId, name, password, gender).also { save(it) }
}

fun AccountRepo.newTeacher(
    name: String,
    password: String,
    teacherId: Int,
): Teacher {
    return Teacher(name, password, teacherId).also { save(it) }
}

fun AccountRepo.newAdministrator(name: String, password: String): Administrator {
    return Administrator(name, password).also { save(it) }
}