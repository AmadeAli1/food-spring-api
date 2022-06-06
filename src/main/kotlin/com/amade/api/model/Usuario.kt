package com.amade.api.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*
import javax.validation.constraints.*

@Table("usuario")
data class Usuario(
    @field:Id @field:Column("uid") val uid: String = UUID.randomUUID().toString(),
    @field:Size(max = 40, min = 2) @field:NotNull @field:Column("username") val name: String,
    @field:NotNull @field:NotBlank @field:Min(value = 6) @field:Column("password") val senha: String,
    @field:Email @field:NotBlank @field:Column("email") val email: String,
    @field:NotNull @field:Column("role") val role: UsuarioRole = UsuarioRole.USER,
) : UserDetails {

    override fun getAuthorities(): List<SimpleGrantedAuthority> {
        val authority = SimpleGrantedAuthority(role.name)
        return listOf(authority)
    }
    override fun getPassword(): String {
        return senha
    }
    override fun getUsername(): String {
        return email
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

}
