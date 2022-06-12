package com.amade.api.controller

import com.amade.api.exception.ApiException
import com.amade.api.model.food.Categoria
import com.amade.api.service.food.CategoriaService
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/food/categoria")
class CategoriaController(
    private val categoriaService: CategoriaService,
) {

    @GetMapping
    suspend fun findAll(): Flow<Categoria> {
        return categoriaService.findAll()
    }

    @PostMapping(
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE,
            MediaType.IMAGE_PNG_VALUE,
            MediaType.IMAGE_JPEG_VALUE,
            MediaType.APPLICATION_JSON_VALUE
        ]
    )
    suspend fun save(
        @RequestPart("file", required = true) image: FilePart,
        @RequestPart("name", required = true) request: String,
    ): ResponseEntity<Categoria> {
        if (request.isBlank().or(request.isNullOrEmpty())) {
            throw ApiException("O {nome} da categoria nao pode ser nulo, e nao pode estar em branco! ")
        }
        if (!image.filename().contains(".")) {
            throw ApiException("Requer uma imagem!")
        }
        val categoria = categoriaService.save(name = request, filePart = image)
        return ResponseEntity(categoria, HttpStatus.CREATED)
    }

    @DeleteMapping
    suspend fun delete(@RequestParam("id", required = true) id: Int): ResponseEntity<String> {
        val status = categoriaService.delete(id)
        return if (status != 1) {
            ResponseEntity(HttpStatus.BAD_REQUEST)
        } else {
            ResponseEntity("Categoria com id: $id foi removida com sucesso", HttpStatus.OK)
        }
    }

}