package com.amade.api.repository

import com.amade.api.model.Image
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ImageRepository :CoroutineCrudRepository<Image,Int>{

}