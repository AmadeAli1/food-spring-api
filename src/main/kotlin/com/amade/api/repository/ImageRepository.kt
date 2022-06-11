package com.amade.api.repository

import com.amade.api.model.Image
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ImageRepository : CoroutineCrudRepository<Image, Int> {

    @Modifying
    @Query("delete from image where id=:id")
    suspend fun deleteImageById(id: Int): Int

}