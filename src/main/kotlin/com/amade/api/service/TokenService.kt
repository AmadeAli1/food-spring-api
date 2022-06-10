package com.amade.api.service

import com.amade.api.exception.ApiRequestException
import com.amade.api.model.Token
import com.amade.api.repository.TokenRepository
import org.apache.commons.logging.Log
import org.slf4j.event.LoggingEvent
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.logging.Logger

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
            throw ApiRequestException(e.message!!)
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
        val status: Int

        if (token.confirmedAt != null) {
            throw ApiRequestException("Token is verified")
        }

        val now = LocalDateTime.now()

        if (token.expiredAt.isBefore(now)) {
            throw ApiRequestException("Token Expirado")
        }

        if (token.expiredAt.isAfter(now).and(token.confirmedAt != null)) {
            status = tokenRepository.confirmToken(usuario_id = token.usuarioId)

            if (status == 1) {
                val enableAccount = tokenRepository.enableAccount(token.usuarioId)
                if (enableAccount == 1) {
                    return "Confirmacao da conta com sucesso"
                }
                throw ApiRequestException("Erro ao habilitar a conta")
            }
        }
        throw ApiRequestException("Nao foi possivel verificar o token!")
    }
}