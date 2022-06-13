package com.amade.api.configurations

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource

@Configuration
class UrlConfiguration {

    @Bean
    fun corsWebFilter(): CorsWebFilter? {
        val corsConfig = CorsConfiguration()
        corsConfig.allowedOrigins = listOf("http://localhost:4200")
        corsConfig.maxAge = 8000L
        corsConfig.allowedMethods = listOf(
            "GET", "PUT", "POST", "DELETE", "OPTION"
        )
        corsConfig.allowedHeaders = listOf(
            "Origin", "Access-Control-Allow-Origin",
            "Content-Type", "Accept", "Authorization", "Origin, Accept", "X-Requested-With", "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        )
        corsConfig.exposedHeaders = listOf(
            "Origin", "Content-Type", "Accept", "Authorization", "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"
        )
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", corsConfig)
        return CorsWebFilter(source)
    }


    companion object {
        @Value(value = "\${food.api.imageUrl}")
        val imageUrl: String? = null
    }

}