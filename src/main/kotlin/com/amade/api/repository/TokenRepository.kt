package com.amade.api.repository

import com.amade.api.model.Token
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface TokenRepository : CoroutineCrudRepository<Token, String> {

    @Modifying
    @Query("insert into token values($1,$2,$3,$4,$5)")
    suspend fun insert(
        id: String,
        usuario_id: String,
        createdAt: LocalDateTime,
        expiredAt: LocalDateTime,
        confirmedAt: LocalDateTime?,
    ): Int

    @Query("select * from token where usuario_id=:usuarioId")
    suspend fun findUserToken(usuario_id: String): Token?

    @Modifying
    @Query("update token set confirmedat=now() where usuario_id=:usuario_id")
    suspend fun confirmToken(usuario_id: String): Int

    @Modifying
    @Query("update usuario set enable=true where uid=:usuarioId")
    suspend fun enableAccount(usuarioId: String): Int
}