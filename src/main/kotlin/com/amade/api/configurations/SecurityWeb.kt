package com.amade.api.configurations

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User

@Configuration
@EnableWebFluxSecurity
class SecurityWeb {

    @Bean
    fun userDetailsService(): ReactiveUserDetailsService {
        val userDetails = User.withDefaultPasswordEncoder()
            .username("Amade")
            .password("amade-2022")
            .roles("USER", "ADMIN")
            .build()
        return MapReactiveUserDetailsService(userDetails)
    }

}