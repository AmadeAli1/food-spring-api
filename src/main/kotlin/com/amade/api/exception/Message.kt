package com.amade.api.exception

import org.springframework.http.HttpStatus

data class Message(
    val message: String,
    val status: String= HttpStatus.OK.name,
)
