package cs309.dormiselect.backend.data

class RestResponse<out T> private constructor(val code: Int, val message: String, val data: T) {
    companion object {
        fun <T> success(data: T, message: String = "Success.") = RestResponse(200, message, data)

        fun <T> fail(code: Int, message: String) = RestResponse<T?>(code, message, null)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RestResponse<*>) return false

        if (code != other.code) return false
        if (message != other.message) return false
        if (data != other.data) return false

        return true
    }

    override fun hashCode(): Int {
        var result = code
        result = 31 * result + message.hashCode()
        result = 31 * result + (data?.hashCode() ?: 0)
        return result
    }

    override fun toString() = "RestResponse(code=$code, message='$message', data=$data)"

    fun copy() = RestResponse(code, message, data)
}

fun <T> T?.asRestResponse(message: String = "Success.") = RestResponse.success(this, message)
