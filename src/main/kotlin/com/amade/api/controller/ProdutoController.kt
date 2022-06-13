package com.amade.api.controller

import com.amade.api.dto.Page
import com.amade.api.dto.ProdutoDTO
import com.amade.api.exception.ValidationRequest
import com.amade.api.model.food.Produto
import com.amade.api.service.food.ProdutoService
import com.fasterxml.jackson.databind.json.JsonMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/api/food/produto")
class ProdutoController(
    private val produtoService: ProdutoService,
    private val validate: ValidationRequest,
) {

    @PostMapping(
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_PNG_VALUE, MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_OCTET_STREAM_VALUE
        ]
    )
    suspend fun save(
        @RequestPart("file") images: Flux<FilePart>,
        @RequestPart("body") body: String,
    ): ResponseEntity<Any> = withContext(Dispatchers.IO) {
        val request = JsonMapper().readValue(body, Produto::class.java)
        val validateRequest = validate.validateRequest(request)

        if (validateRequest != null) {
            return@withContext validateRequest
        }

        val produto = produtoService.save(files = images.toStream().toList(), request = request)
        ResponseEntity(produto, HttpStatus.CREATED)
    }

    @GetMapping("/all")
    suspend fun findAll(): Flow<ProdutoDTO> {
        return produtoService.findAll()
    }


    @GetMapping
    suspend fun find(
        @RequestParam("page", defaultValue = "0") page: Int,
    ): ResponseEntity<Page<ProdutoDTO>> {
        val body = produtoService.pagination(page = page)
        return ResponseEntity(body, HttpStatus.OK)
    }

}