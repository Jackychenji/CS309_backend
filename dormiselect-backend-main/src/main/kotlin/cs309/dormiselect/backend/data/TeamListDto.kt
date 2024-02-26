package cs309.dormiselect.backend.data

data class TeamListDto(
    val total: Int,
    val page: Int,
    val pageSize: Int,
    val rows: List<Any>,
)
