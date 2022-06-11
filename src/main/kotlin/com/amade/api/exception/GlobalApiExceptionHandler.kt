package com.amade.api.exception

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebExceptionHandler
import reactor.core.publisher.Mono


@Component
@EnableWebFlux
class GlobalApiExceptionHandler {

    @Bean
    fun exceptionHandler(): WebExceptionHandler? {
        return WebExceptionHandler { exchange: ServerWebExchange, ex: Throwable? ->
            if (ex is ApiException) {
                exchange.attributes["message"] = ex.message
                return@WebExceptionHandler exchange.response.setComplete()
            }
            Mono.error(ex!!)
        }
    }

}