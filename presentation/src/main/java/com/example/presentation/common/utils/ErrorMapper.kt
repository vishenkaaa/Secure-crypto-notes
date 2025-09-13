package com.example.presentation.common.utils

import com.example.domain.model.exception.BaseException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object ErrorMapper {

    fun mapError(e: Throwable): BaseException {
        return when (e) {
            is UnknownHostException,
            is IOException -> BaseException.ConnectionErrorException

            is SocketTimeoutException -> BaseException.TimeoutException

            is HttpException -> when (e.code()) {
                400 -> BaseException.ApiErrorException(400, "Bad Request")
                401 -> BaseException.UnauthorizedException
                403 -> BaseException.ForbiddenException
                404 -> BaseException.NotFoundException
                500 -> BaseException.ServerErrorException
                else -> BaseException.ApiErrorException(e.code(), e.message())
            }

            else -> BaseException.UnknownException(e.message ?: "Unknown error")
        }
    }
}