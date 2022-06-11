package com.amade.api.service

import com.amade.api.exception.ApiException
import com.amade.api.model.Token
import com.amade.api.repository.TokenRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TokenService(
    private val tokenRepository: TokenRepository,
) {
    suspend fun createToken(usuarioId: String): String? = withContext(Dispatchers.IO) {
        val userToken = Token(usuarioId = usuarioId)
        try {
            val status = tokenRepository.insert(
                id = userToken.token,
                usuario_id = userToken.usuarioId,
                createdAt = userToken.createdAt, expiredAt = userToken.expiredAt, confirmedAt = userToken.confirmedAt
            )
            if (status == 1) {
                return@withContext userToken.token
            }
            return@withContext null
        } catch (e: Exception) {
            throw ApiException("Ocorreu um erro ao Criar o token! cause{${e.message}}")
        }
    }

    private suspend fun findTokenById(tokenId: String): Token? {
        return tokenRepository.findById(id = tokenId)
    }

    suspend fun confirmAccount(id: String): String? {
        val token = findTokenById(tokenId = id)!!
        val status: Int

        if (token.confirmedAt != null) {
            throw ApiException("O Token ja foi confirmado!")
        }

        val now = LocalDateTime.now()
        if (token.expiredAt.isBefore(now)) {
            throw ApiException("O Token expirou!")
        }
        if (token.expiredAt.isAfter(now)) {
            status = tokenRepository.confirmToken(usuario_id = token.usuarioId)

            if (status == 1) {
                try {
                    val enableAccount = tokenRepository.enableAccount(token.usuarioId)
                    if (enableAccount == 1) {
                        return "Confirmacao da conta com sucesso!"
                    }
                } catch (e: Exception) {
                    throw ApiException("Ocorreu um erro ao habilitar a conta! cause{${e.message}}")
                }
            }
        }
        throw ApiException("Nao foi possivel verificar o token!")
    }
}