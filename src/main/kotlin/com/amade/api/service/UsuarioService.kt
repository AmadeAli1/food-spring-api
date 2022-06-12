package com.amade.api.service

import com.amade.api.configurations.UrlConfiguration.Companion.confirmTokenUrl
import com.amade.api.configurations.UrlConfiguration.Companion.imageUrl
import com.amade.api.dto.UsuarioDTO
import com.amade.api.exception.ApiException
import com.amade.api.model.Usuario
import com.amade.api.repository.UsuarioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UsuarioService(
    private val usuarioRepository: UsuarioRepository,
    private val passwordEncoder: PasswordEncoder,
    private val imageService: ImageService,
    private val emailService: EmailService,
    private val tokenService: TokenService,
) {
    suspend fun register(usuario: Usuario): UsuarioDTO? = withContext(Dispatchers.IO) {
        val exists = usuarioRepository.existsByEmail(email = usuario.email)
        if (exists) {
            throw ApiException("Este email:{${usuario.email} ja esta em uso!}")
        }
        val senha = encode(usuario.senha)
        val status: Int
        try {
            status = usuarioRepository.insert(
                usuario.uid, usuario.name, senha, usuario.email, usuario.role.name
            )
        } catch (e: Exception) {
            throw ApiException("Ocorreu um erro ao gravar o cliente! cause{${e.message}}")
        }
        return@withContext if (status == 1) {
            val us = findById(usuario.uid)!!
            sendEmailConfirmation(us)
            UsuarioDTO(uid = us.uid, email = us.email, username = us.name, isEnable = us.enable, us.imageUrl)
        } else {
            null
        }
    }

    private suspend fun sendEmailConfirmation(us: Usuario) {
        val token = tokenService.createToken(usuarioId = us.uid)
        emailService.sendEmail(
            sendToEmail = us.email, subject = "Food Market", body = """
                    Ola ${us.name} obrigado por ciar uma conta na Food Market
                    Para confirmar a sua conta: ${confirmTokenUrl}?token=${token!!}
                    Token Valido para 40 minutos
                """.trimIndent()
        )
    }

    suspend fun loginByEmail(email: String, senha: String): UsuarioDTO? {
        val us = usuarioRepository.findUsuarioByEmail(email = email)
            ?: throw ApiException("O Email {$email} introduzido nao existe!")
        if (decode(us.senha, senha)) {
            return UsuarioDTO(uid = us.uid, email = us.email, username = us.name, isEnable = us.enable, us.imageUrl)
        }
        return null
    }

    private fun encode(senha: String): String {
        return passwordEncoder.encode(senha)
    }

    private fun decode(encodeSenha: String, senha: String): Boolean {
        return passwordEncoder.matches(senha, encodeSenha)
    }

    suspend fun findAllUsers() = usuarioRepository.findAll().map {
        UsuarioDTO(it.uid, it.email, it.name, it.enable, it.imageUrl)
    }

    private suspend fun findById(uid: String): Usuario? {
        return usuarioRepository.findById(uid)
    }

    fun findByUsername(username: String?): Mono<UserDetails> {
        return usuarioRepository.getByEmail(username!!)
    }

    suspend fun addProfilePicture(usuarioId: String, profileUrl: String?): UsuarioDTO {
        return withContext(Dispatchers.IO) {
            val status = usuarioRepository.addProfilePicture(usuarioId, profileUrl)
            if (status == 1) {
                val usuario = findById(usuarioId)!!
                return@withContext UsuarioDTO(
                    usuario.uid, usuario.email, usuario.name, usuario.enable, usuario.imageUrl
                )
            }
            throw ApiException("Ocorreu um erro ao Mudar a photo do perfil")
        }
    }


    suspend fun deleteProfilePicture(usuarioId: String): UsuarioDTO? = withContext(Dispatchers.IO) {
        val usuario = findById(uid = usuarioId)
        if (usuario?.imageUrl != null) {
            val ss = usuario.imageUrl.drop(imageUrl!!.length)
            val statusUpdate = usuarioRepository.addProfilePicture(usuario.uid, null)
            val statusDelete = imageService.deleteById(id = ss.toInt())

            if (statusDelete == 1 && statusUpdate == 1) {
                return@withContext UsuarioDTO(usuario.uid, usuario.email, usuario.name, usuario.enable, null)
            }
            throw ApiException("Ocorreu um erro ao remover a imagem do perfil")
        }
        return@withContext null
    }

}
