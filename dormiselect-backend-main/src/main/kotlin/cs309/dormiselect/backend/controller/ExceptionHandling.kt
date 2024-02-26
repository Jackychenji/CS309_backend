package cs309.dormiselect.backend.controller

import cs309.dormiselect.backend.data.RestResponse
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice("cs309.dormiselect.backend.controller")
class ExceptionHandling : ResponseEntityExceptionHandler() {
    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(e: ResponseStatusException) =
        RestResponse.fail<Any>(e.statusCode.value(), e.reason ?: "Unknown error.")

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(e: AccessDeniedException) =
        RestResponse.fail<Any>(403, e.message ?: "Unknown error.")

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: IllegalArgumentException) =
        RestResponse.fail<Any>(400, e.message ?: "Unknown error.")

    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalStateException(e: IllegalStateException) =
        RestResponse.fail<Any>(400, e.message ?: "Unknown error.")

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception) = RestResponse.fail<Any>(-1, e.message ?: "Unknown error.")
}