package com.amade.api.model

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

@Table("password_change")
data class PasswordToken(
    @field:Id @field:Column("id") val id: String = UUID.randomUUID().toString(),
    @field:Column("userId") val usuarioId: String,
    @field:Column("created") val createdAt: LocalDateTime = LocalDateTime.now(),
    @field:Column("expired") val expiredAt: LocalDateTime = createdAt.plus(40L, ChronoUnit.MINUTES),
    @field:Column("confirmed") var confirmed: Boolean? = false,
    @field:Column("blocked") var blocked: Boolean? = false
) {
}