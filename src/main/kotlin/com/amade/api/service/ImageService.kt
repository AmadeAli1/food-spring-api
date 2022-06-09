package com.amade.api.service

import com.amade.api.exception.ApiRequestException
import com.amade.api.model.Image
import com.amade.api.repository.ImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils


@Service
class ImageService(
    private val imageRepository: ImageRepository,
) {

    suspend fun save(file: FilePart): Image? {
        return withContext(Dispatchers.IO) {
            val name = StringUtils.cleanPath(file.filename())
            try {
                if (name.contains("..")) {
                    throw ApiRequestException("Filename invalid")
                }

                val type = if (name.endsWith("jpg")) {
                    MediaType.IMAGE_JPEG_VALUE
                } else {
                    MediaType.IMAGE_PNG_VALUE
                }

                val image = Image(
                    image = file.content().blockFirst()!!.asByteBuffer().array(),
                    type = type,
                    name = name
                )

                return@withContext imageRepository.save(image)
            } catch (e: Exception) {
                println(e.message)
                null
            }
        }

    }

    suspend fun findAllImages() = withContext(Dispatchers.IO) {
        imageRepository.findAll()
    }

    suspend fun findByid(id: Int) = imageRepository.findById(id)

}