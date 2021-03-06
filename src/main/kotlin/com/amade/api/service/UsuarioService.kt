package com.amade.api.service

import com.amade.api.configurations.UrlConfiguration.Companion.imageUrl
import com.amade.api.dto.UsuarioDTO
import com.amade.api.exception.ApiException
import com.amade.api.model.Usuario
import com.amade.api.repository.UsuarioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Value
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
    @Value(value = "\${source.food.api.token}")
    val confirmTokenUrl: String? = null

    suspend fun register(usuario: Usuario): UsuarioDTO? = withContext(Dispatchers.IO) {
        val exists = usuarioRepository.existsByEmail(email = usuario.email)
        if (exists) {
            throw ApiException("This email already exists")
        }
        val senha = encode(usuario.senha)
        val status: Int
        try {
            status = usuarioRepository.insert(
                usuario.uid, usuario.name, senha, usuario.email, usuario.role.name
            )
        } catch (e: Exception) {
            throw ApiException("Invalid data")
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
                    Hi ${us.name} thank you for your account at Food Market
                    To confirm your account: ${confirmTokenUrl}?token=${token!!}
                    Valid Token for 40 minutes
                """.trimIndent()
        )
    }

    suspend fun loginByEmail(email: String, senha: String): UsuarioDTO? {
        val us = usuarioRepository.findUsuarioByEmail(email = email)
            ?: throw ApiException("Account not found")

        if (!decode(us.senha, senha)){
            throw ApiException("Invalid password")
        }
        return if (decode(us.senha, senha)) {
            UsuarioDTO(uid = us.uid, email = us.email, username = us.name, isEnable = us.enable, us.imageUrl)
        }else{
            null
        }
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
            throw ApiException("An error occurred when changing the profile photo")
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
            throw ApiException("An error occurred while removing the profile picture")
        }
        return@withContext null
    }

}
