package com.amade.api.configurations

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class Password {

    @Bean
    fun BCryptEnconder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

}