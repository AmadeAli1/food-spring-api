package com.amade.api.controller

import com.amade.api.service.ImageService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/image")
@RestController
class ImageController(
    private val imageService: ImageService,
) {

    @PostMapping(
        "/save",
        consumes = [
            MediaType.MULTIPART_FORM_DATA_VALUE,
            MediaType.IMAGE_PNG_VALUE,
            MediaType.IMAGE_JPEG_VALUE
        ]
    )
    suspend fun save(@RequestPart("file") file: FilePart): ResponseEntity<String>? {
        val linkDownload = imageService.save(file)
        return ResponseEntity(linkDownload, HttpStatus.CREATED)
    }

    @GetMapping("/download")
    suspend fun getImage(@RequestParam("id") id: Int): ResponseEntity<Resource> {
        return withContext(Dispatchers.IO) {
            val img = imageService.findByid(id)
            ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(img!!.type))
                .header(HttpHeaders.CONTENT_TYPE)
                .body(ByteArrayResource(img.image))
        }
    }

    @GetMapping("/all")
    suspend fun findAllImagesUrl() = imageService.findAllImages()


}