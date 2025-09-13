package com.example.domain.model.exception

sealed class BaseException(open val error: String? = null) : Exception(error) {
    // API errors
    data class ApiErrorException(val code: Int, override val error: String?) : BaseException(error)
    data object UnauthorizedException : BaseException("Unauthorized")
    data object ForbiddenException : BaseException("Forbidden")
    data object TooManyRequestsException : BaseException("Rate limit exceeded")
    data object ServerErrorException : BaseException("Internal server error")
    data object ServiceUnavailableException : BaseException("Service unavailable")

    // Network errors
    data object ConnectionErrorException : BaseException("No internet connection")
    data object TimeoutException : BaseException("Request timed out")

    // Local errors
    data class DatabaseException(override val error: String?) : BaseException(error)
    data object NotFoundException : BaseException("Resource not found")

    data class UnknownException(override val error: String?) : BaseException(error)
}
