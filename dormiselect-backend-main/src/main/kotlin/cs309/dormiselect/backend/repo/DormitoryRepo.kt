package cs309.dormiselect.backend.repo

import cs309.dormiselect.backend.domain.Dormitory
import cs309.dormiselect.backend.domain.account.Student
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface DormitoryRepo : CrudRepository<Dormitory, Int> {
    fun findByBuildingId(buildingId: Int): List<Dormitory>

    //CrudRepository already has a function findById(id: ID): Optional<T>
    //fun findById(dormitoryId: String): Dormitory?
    fun findBySize(size: Int): List<Dormitory>
    fun existsByRoomIdAndZoneIdAndBuildingId(roomId: Int, zoneId: Int, buildingId: Int): Boolean
    fun findAll(pageable: Pageable): Page<Dormitory>
    @Query("SELECT d FROM Dormitory d WHERE (:zoneId = 0 OR d.zoneId = :zoneId) AND (:buildingId = 0 OR d.buildingId = :buildingId) AND (:size = 0 OR d.size = :size)")
    fun findByZoneIdBuildingIdAndSizeWithZoneIdCheck(
        @Param("zoneId") zoneId: Int,
        @Param("buildingId") buildingId: Int,
        @Param("size") size: Int,
        pageable: Pageable
    ): Page<Dormitory>

    fun findByRoomIdAndZoneIdAndBuildingId(
    @Param("roomId") roomId: Int,
    @Param("zoneId") zoneId: Int,
    @Param("buildingId") buildingId: Int
    ): Dormitory?

    fun findByZoneIdAndGender(zoneId: Int, gender: Student.Gender): List<Dormitory>

}

fun DormitoryRepo.newDormitory(roomId: Int, zoneId: Int, size: Int, buildingId: Int, gender: Student.Gender,info: String) =
    Dormitory(roomId, zoneId, size, buildingId, gender, info).also { save(it) }
