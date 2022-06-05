package com.amade.api.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.support.WebExchangeBindException

@ControllerAdvice
class ValidationExceptionHandler {

    @ExceptionHandler(WebExchangeBindException::class)
    suspend fun invalidRequest(e: WebExchangeBindException): ResponseEntity<List<ErrorMessage.ErrorAtribute>> {
        println("Called")
        val erros = e.bindingResult.allErrors.stream().map {
            lateinit var errorAtribute: ErrorMessage.ErrorAtribute
            if (it is FieldError) {
                errorAtribute = ErrorMessage.ErrorAtribute(field = it.field, message = it.defaultMessage)
            }
            errorAtribute
        }.toList()
        return ResponseEntity(erros, HttpStatus.BAD_REQUEST)
    }

}