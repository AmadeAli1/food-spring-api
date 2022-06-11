package com.amade.api.configurations

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class UrlConfiguration {
    companion object {
        @Value(value = "\${food.api.imageUrl}")
        val imageUrl: String? = null

        @Value(value = "\${source.food.api.token}")
        val confirmTokenUrl: String? = null
    }
}