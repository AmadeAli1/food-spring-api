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

    suspend fun save(usuario: Usuario): Usuario {
        val senha = passwordEncoder.encode(usuario.senha)

        val status = usuarioRepository.insert(
            usuario.uid, usuario.name, senha!!, usuario.email, usuario.role.name
        )

        return if (status == 1) {
            findById(usuario.uid)!!
        } else {
            throw RuntimeException("Erro ao gravar")
        }
    }

    suspend fun findById(uid: String): Usuario? {
        return usuarioRepository.findById(uid)
    }

}