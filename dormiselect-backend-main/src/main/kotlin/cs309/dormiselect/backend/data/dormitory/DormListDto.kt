package cs309.dormiselect.backend.data.dormitory

import cs309.dormiselect.backend.domain.Dormitory

data class DormListDto(
    val total: Int,
    val page: Int,
    val pageSize: Int,
    val rows: List<Any>,
)
