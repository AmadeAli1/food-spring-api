package com.amade.api.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ApiExceptionHandler {

    @ExceptionHandler(value = [ApiRequestException::class])
    fun handlerException(exception: ApiRequestException): ResponseEntity<Any> {
        val apiException = ApiRequestException.ApiExceptionBody(
            message = exception.message!!,
            cause = exception.cause!!,
            httpStatus = HttpStatus.BAD_REQUEST
        )
        return ResponseEntity(apiException, HttpStatus.BAD_REQUEST)
    }

}