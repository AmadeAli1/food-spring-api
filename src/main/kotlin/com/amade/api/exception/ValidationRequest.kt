package com.amade.api.exception

import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import javax.validation.Validator

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
class ValidationRequest(
    private val validator: Validator,
) {

    fun <T> validateRequest(request: T): ResponseEntity<Any>? {
        val validate = validator.validate(request)
        if (validate.isNotEmpty()) {
            val erros = validate.map {
                val errorAtribute = ErrorMessage.ErrorAtribute(field = it.propertyPath.toString(), message = it.message)
                errorAtribute
            }.toList()
            return ResponseEntity(erros, HttpStatus.BAD_REQUEST)
        }
        return null
    }

}