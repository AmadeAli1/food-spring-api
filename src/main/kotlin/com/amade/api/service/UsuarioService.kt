package com.amade.api.service

import com.amade.api.model.Usuario
import com.amade.api.repository.UsuarioRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UsuarioService(
    private val usuarioRepository: UsuarioRepository,
    private val passwordEncoder: PasswordEncoder,
) {

    suspend fun register(usuario: Usuario): Usuario? {
        val senha = encode(usuario.senha)
        var status = 0
        try {
            status = usuarioRepository.insert(
                usuario.uid, usuario.name, senha, usuario.email, usuario.role.name
            )
        } catch (e: Exception) {
            println(e.message)
            return null
        }
        return if (status == 1) {
            findById(usuario.uid)!!
        } else {
            null
        }
    }

    suspend fun loginByEmail(email: String, senha: String): Usuario? {
        val usuario = usuarioRepository.findUsuarioByEmail(email = email)

        if (decode(usuario.senha, senha)) {
            return usuario
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

}