package cs309.dormiselect.backend.repo

import cs309.dormiselect.backend.domain.account.Teacher
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository

interface TeacherRepo : CrudRepository<Teacher, Int> {
    fun findAll(pageable: Pageable): Page<Teacher>

    fun existsByTeacherId(teacherId: Int): Boolean

    fun findByTeacherId(teacherId: Int): Teacher?
}

fun TeacherRepo.newTeacher(teacherId: Int, name: String, password: String) =
    Teacher(name, password, teacherId).also { save(it) }
