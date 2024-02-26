package cs309.dormiselect.backend.repo

import cs309.dormiselect.backend.domain.account.Administrator
import org.springframework.data.repository.CrudRepository

interface AdministratorRepo : CrudRepository<Administrator, Int> {
    fun findByAdministratorId(administratorId: Int): Administrator?
}