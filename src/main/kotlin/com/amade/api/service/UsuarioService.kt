package com.amade.api.service

import com.amade.api.dto.UsuarioDTO
import com.amade.api.exception.ApiRequestException
import com.amade.api.model.Usuario
import com.amade.api.repository.UsuarioRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UsuarioService(
    private val usuarioRepository: UsuarioRepository,
    private val passwordEncoder: PasswordEncoder,
    private val emailService: EmailService,
    private val tokenService: TokenService,
) {
    @Value(value = "\${source.food.api.token}")
    val confirmTokenUrl: String? = null

    suspend fun register(usuario: Usuario): UsuarioDTO? {
        val senha = encode(usuario.senha)
        val status: Int
        try {
            status = usuarioRepository.insert(
                usuario.uid, usuario.name, senha, usuario.email, usuario.role.name
            )
        } catch (e: Exception) {
            throw ApiRequestException(e.message!!)
        }
        return if (status == 1) {
            val us = findById(usuario.uid)!!
            sendEmailConfirmation(us)
            UsuarioDTO(uid = us.uid, email = us.email, username = us.name, isEnable = us.enable)
        } else {
            null
        }
    }

    private suspend fun sendEmailConfirmation(us: Usuario) {
        val token = tokenService.createToken(usuarioId = us.uid)
        emailService.sendEmail(
            sendToEmail = us.email, subject = "Food Market", body =
            """
                    Ola ${us.name} obrigado por ciar uma conta na Food Market
                    Para confirmar a sua conta: ${confirmTokenUrl}?token=${token!!}
                    Token Valido para 40 minutos
                """.trimIndent()
        )
    }

    suspend fun loginByEmail(email: String, senha: String): UsuarioDTO? {
        val us = usuarioRepository.findUsuarioByEmail(email = email)
        if (decode(us.senha, senha)) {
            return UsuarioDTO(uid = us.uid, email = us.email, username = us.name, isEnable = us.enable)
        }
        return null
    }

    private fun encode(senha: String): String {
        return passwordEncoder.encode(senha)
    }

    private fun decode(encodeSenha: String, senha: String): Boolean {
        return passwordEncoder.matches(senha, encodeSenha)
    }

    private suspend fun findById(uid: String): Usuario? {
        return usuarioRepository.findById(uid)
    }

    fun findByUsername(username: String?): Mono<UserDetails> {
        return usuarioRepository.getByEmail(username!!)
    }
}