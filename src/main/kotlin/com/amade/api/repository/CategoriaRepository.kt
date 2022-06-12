package com.amade.api.repository

import com.amade.api.model.food.Categoria
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CategoriaRepository : CoroutineCrudRepository<Categoria, Int> {

    @Modifying
    @Query("DELETE FROM categoria where id=:id")
    suspend fun delete(id: Int): Int

    @Modifying
    @Query("INSERT INTO categoria (nome, imagem) VALUES (:?1,:?2)")
    suspend fun save(name: String, image: String): Int

}