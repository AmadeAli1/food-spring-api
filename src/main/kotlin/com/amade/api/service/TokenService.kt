package com.amade.api.service

import com.amade.api.model.Token
import com.amade.api.repository.TokenRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TokenService(
    private val tokenRepository: TokenRepository,
) {
    suspend fun createToken(usuarioId: String): String? {
        val userToken = Token(usuarioId = usuarioId)
        try {
            val status = tokenRepository.insert(
                id = userToken.token,
                usuario_id = userToken.usuarioId,
                createdAt = userToken.createdAt, expiredAt = userToken.expiredAt, confirmedAt = userToken.confirmedAt
            )
            if (status == 1) {
                return userToken.token
            }
            return null
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private suspend fun findToken(usuarioId: String): Token? {
        return tokenRepository.findUserToken(usuarioId)
    }

    private suspend fun findTokenById(tokenId: String): Token? {
        return tokenRepository.findById(id = tokenId)
    }

    suspend fun confirmAccount(id: String): String? {
        val token = findTokenById(tokenId = id)!!
        var status = 0

        if (token.confirmedAt != null) {
            throw RuntimeException("Token is verified")
        }

        val now = LocalDateTime.now()

        if (token.expiredAt.isBefore(now)) {

            throw RuntimeException("Token Expirado")
        }

        if (token.expiredAt.isAfter(now) || token.expiredAt.isEqual(now)) {
            status = tokenRepository.confirmToken(now, usuario_id = token.usuarioId)
            if (status == 1) {
                tokenRepository.enableAccount(token.usuarioId)
                return "Confirmacao da conta com sucesso"
            }
        }
        return null
    }
}