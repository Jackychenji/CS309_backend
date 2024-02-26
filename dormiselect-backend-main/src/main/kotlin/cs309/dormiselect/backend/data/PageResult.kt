package cs309.dormiselect.backend.data

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

data class PageResult<out T>(
    val total: Int,
    val page: Int,
    val pageSize: Int,
    val rows: List<T>
)

fun <T> Page<T>.toPageResult(): PageResult<T> {
    return PageResult(totalPages, number, size, content)
}

fun <T> Iterable<T>.toPageResult(pageable: Pageable): PageResult<T> = PageResult(
    count(),
    pageable.pageNumber,
    pageable.pageSize,
    chunked(pageable.pageSize).getOrNull(pageable.pageNumber) ?: listOf()
)

fun <T> Sequence<T>.toPageResult(pageable: Pageable) = asIterable().toPageResult(pageable)

