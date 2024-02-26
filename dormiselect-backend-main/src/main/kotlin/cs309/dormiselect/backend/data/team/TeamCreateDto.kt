package cs309.dormiselect.backend.data.team


data class TeamCreateDto(
    val name: String,
    val maxSize: Int,
    val recruiting: Boolean,
    val introduction: String,
)
