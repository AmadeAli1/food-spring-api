package com.amade.api.configurations

import com.amade.api.service.UsuarioService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.web.server.SecurityWebFilterChain


@Configuration
@EnableWebFluxSecurity
class SecurityWeb(
    private val service: UsuarioService,
) {

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain? {
        http
            .authorizeExchange()
            .pathMatchers(HttpMethod.GET, "/api/**").permitAll()
            .pathMatchers(HttpMethod.DELETE, "/api/**").hasAuthority("USER")
            .pathMatchers(HttpMethod.POST, "/api/register").permitAll()
            .pathMatchers(HttpMethod.POST, "/api/**").hasAuthority("ADMIN")
            .anyExchange()
            .authenticated()
            .and()
            .httpBasic()
            .and()
            .csrf().disable()
            .formLogin()
        return http.build()
    }

    @Bean
    fun userDetailsService(): ReactiveUserDetailsService? {
        return ReactiveUserDetailsService { username: String? ->
            service.findByUsername(username!!)
        }
    }


}