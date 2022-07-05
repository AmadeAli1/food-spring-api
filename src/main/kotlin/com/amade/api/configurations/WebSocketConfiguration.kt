package com.amade.api.configurations

import com.amade.api.websocket.ProdutoWebSocketHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter

@Configuration
class WebSocketConfiguration(
    private val produtoWebSocketHandler: ProdutoWebSocketHandler,
) {

    @Bean
    fun handlerMapping(): SimpleUrlHandlerMapping {
        val map = mutableMapOf<String, WebSocketHandler>()
        map["/ws/new/produto"] = produtoWebSocketHandler
        return SimpleUrlHandlerMapping(map, 1)
    }

    @Bean
    fun webSocketHandlerAdapter(): WebSocketHandlerAdapter {
        return WebSocketHandlerAdapter()
    }

}