package cs309.dormiselect.backend.repo

import cs309.dormiselect.backend.domain.account.Student
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository


interface StudentRepo : CrudRepository<Student, Int> {
    fun findByName(name: String): Student

    fun findAll(pageable: Pageable): Page<Student>

    fun existsByStudentId(studentId: Int): Boolean
    fun findByStudentId(studentId: Int): Student?


}
