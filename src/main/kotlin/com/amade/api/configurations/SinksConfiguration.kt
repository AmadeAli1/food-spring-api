package com.amade.api.configurations

import com.amade.api.dto.ProdutoDTO
import com.amade.api.dto.UsuarioDTO
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.json.JsonMapper
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import reactor.core.publisher.Sinks

@Component
class SinksConfiguration {

    @Bean
    fun sink(): Sinks.Many<ProdutoDTO> {
        return Sinks.many().multicast().directBestEffort()
    }

    @Bean
    fun sink_user(): Sinks.Many<UsuarioDTO> {
        return Sinks.many().multicast().directBestEffort()
    }


    @Bean
    fun mapper(): ObjectMapper {
        return JsonMapper()
    }

}