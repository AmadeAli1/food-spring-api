package com.amade.api.service

import com.amade.api.dto.UsuarioDTO
import com.amade.api.model.Usuario
import com.amade.api.repository.UsuarioRepository
import com.amade.api.utils.Constantes.confirmTokenUrl
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

    suspend fun register(usuario: Usuario): UsuarioDTO? {
        val senha = encode(usuario.senha)
        val status: Int
        try {
            status = usuarioRepository.insert(
                usuario.uid, usuario.name, senha, usuario.email, usuario.role.name
            )
        } catch (e: Exception) {
            println(e.message)
            return null
        }
        return if (status == 1) {
            val us = findById(usuario.uid)!!

            val token = tokenService.createToken(usuarioId = us.uid)

            emailService.sendEmail(
                sendToEmail = us.email, subject = "Food Market", body =
                """
                Ola ${us.name} obrigado por ciar uma conta na Food Market
                Para confirmar a sua conta: ${confirmTokenUrl}?token=${token!!}
                Token Valido para 15 minutos
            """.trimIndent()
            )
            UsuarioDTO(uid = us.uid, email = us.email, username = us.name)
        } else {
            null
        }
    }

    suspend fun loginByEmail(email: String, senha: String): UsuarioDTO? {
        val us = usuarioRepository.findUsuarioByEmail(email = email)

        if (decode(us.senha, senha)) {
            return UsuarioDTO(uid = us.uid, email = us.email, username = us.name)
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