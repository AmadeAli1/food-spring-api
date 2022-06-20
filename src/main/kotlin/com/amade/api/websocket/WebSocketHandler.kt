package com.amade.api.websocket

import com.amade.api.dto.ProdutoDTO
import com.amade.api.dto.UsuarioDTO
import com.amade.api.model.Usuario
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks.Many

@Component
class ProdutoWebSocketHandler(
    private val sinks: Many<ProdutoDTO>,
    private val mapper: ObjectMapper,
) : WebSocketHandler {

    override fun handle(session: WebSocketSession): Mono<Void> {
        val data = sinks.asFlux()
            .map { produto ->
                session.textMessage(mapper.writeValueAsString(produto))
            }
        return session.send(data)
    }

}

@Component
class UsuarioWebSocketHandler(
    private val sinks: Many<UsuarioDTO>,
    private val mapper: ObjectMapper,
) : WebSocketHandler {

    override fun handle(session: WebSocketSession): Mono<Void> {
        val data = sinks.asFlux()
            .map { produto ->
                session.textMessage(mapper.writeValueAsString(produto))
            }
        return session.send(data)
    }
}

