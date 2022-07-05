package com.amade.api.service

import com.amade.api.dto.ImageDTO
import com.amade.api.exception.ApiException
import com.amade.api.model.Image
import com.amade.api.repository.ImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.util.*


@Service
class ImageService(
    private val imageRepository: ImageRepository,
) {
    @Value(value = "\${food.api.imageUrl}")
    val imageUrl: String? = null

    suspend fun save(file: FilePart): String? {
        return withContext(Dispatchers.IO) {
            val name = StringUtils.cleanPath(file.filename())
            try {
                if (name.contains("..")) {
                    throw ApiException("Filename invalid")
                }

                val type = if (name.endsWith(".jpg")) {
                    MediaType.IMAGE_JPEG_VALUE
                } else {
                    MediaType.IMAGE_PNG_VALUE
                }
                val img = file.content().blockFirst()!!.asInputStream().readAllBytes()
                val image = Image(
                    image = img,
                    type = type,
                    name = name + UUID.randomUUID().toString()
                )
                return@withContext imageUrl + "${imageRepository.save(image).id}"
            } catch (e: Exception) {
                throw ApiException("An error occurred while recording an image! =>:{${e.message!!}}")
            }
        }

    }

    suspend fun findAllImages() = withContext(Dispatchers.IO) {
        withContext(Dispatchers.Default) {
            imageRepository.findAll().map {
                ImageDTO(imageUrl = imageUrl + "${it.id}")
            }
        }
    }

    suspend fun findByid(id: Int) = withContext(Dispatchers.IO) {
        imageRepository.findById(id)
    }

    suspend fun deleteById(id: Int) = withContext(Dispatchers.IO) {
        imageRepository.deleteImageById(id)
    }

}