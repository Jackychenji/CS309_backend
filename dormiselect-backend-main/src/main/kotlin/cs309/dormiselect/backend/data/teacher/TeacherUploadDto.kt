package cs309.dormiselect.backend.data.teacher

data class TeacherUploadDto(
    val teacherId: Int,
    val name: String,
    val password: String,
    val buildingInCharge: Int,
)
