@file:Suppress("SpringDataRepositoryMethodReturnTypeInspection")

package com.amade.api.repository

import com.amade.api.model.Usuario
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface UsuarioRepository : CoroutineCrudRepository<Usuario, String> {

    @Modifying
    @Query("insert into usuario values($1,$2,$3,$4,$5)")
    suspend fun insert(uid: String, name: String, senha: String, email: String, role: String): Int

    @Query("select * from usuario where email=:email")
    suspend fun findUsuarioByEmail(email: String): Usuario


    @Query("select * from usuario where email=:email")
    fun getByEmail(email: String): Mono<UserDetails>


}