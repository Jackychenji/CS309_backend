package cs309.dormiselect.backend.data.dormitory

import org.springframework.web.bind.annotation.RequestParam

data class DormPageRequestDto(
    val page: Int,
    val pageSize: Int,
    val zoneId: Int,
    val buildingId: Int,
    val size: Int,
)
