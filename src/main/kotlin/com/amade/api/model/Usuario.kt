package com.amade.api.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Table("usuario")
data class Usuario(
    @field:Id @field:Column("uid") val uid: String = UUID.randomUUID().toString(),
    @field:Size(max = 40, min = 2) @field:NotNull @NotBlank @field:Column("username") val name: String,
    @field:NotNull @field:Size(min = 6, max = 60) @NotBlank @field:Column("password") val senha: String,
    @field:Email @field:NotBlank @field:Column("email") val email: String,
    @field:Column("imageurl") val imageUrl: String? = null,
    @field:NotNull @field:Column("role") val role: UsuarioRole = UsuarioRole.USER,
    @field:NotNull @field:Column("enable") var enable: Boolean = false,
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
        return enable
    }

}
