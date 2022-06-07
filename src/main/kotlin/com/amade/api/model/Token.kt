package com.amade.api.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

@Table("token")
data class Token(
    @field:Id @field:Column("id") val token: String = UUID.randomUUID().toString(),
    @field:Column("usuario_id") val usuarioId: String,
    @field:Column("createdat") val createdAt: LocalDateTime = LocalDateTime.now(),
    @field:Column("expiredat") val expiredAt: LocalDateTime = createdAt.plus(40L, ChronoUnit.MINUTES),
    @field:Column("confirmedat") var confirmedAt: LocalDateTime? = null,
)
