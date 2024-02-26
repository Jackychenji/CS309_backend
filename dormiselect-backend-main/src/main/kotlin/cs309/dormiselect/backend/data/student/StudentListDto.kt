package cs309.dormiselect.backend.data.student

import cs309.dormiselect.backend.domain.account.Student

data class StudentListDto(
    val total: Int,
    val page: Int,
    val pageSize: Int,
    val rows: List<Student>,

)
