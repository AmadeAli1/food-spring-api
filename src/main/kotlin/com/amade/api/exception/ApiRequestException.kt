package com.amade.api.exception

import org.springframework.http.HttpStatus
import java.sql.Timestamp

class ApiRequestException(message: String) : RuntimeException(message,) {

    constructor() : this("")

    data class ApiExceptionBody(
        val message: String,
        val httpStatus: HttpStatus,
        val cause: Throwable,
        val dateTime: Timestamp = Timestamp(System.currentTimeMillis()),
    )
}