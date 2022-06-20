package com.amade.api.controller

import com.amade.api.dto.Page
import com.amade.api.dto.ProdutoDTO
import com.amade.api.exception.Message
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


    @PostMapping("/wishlist")
    suspend fun addToWishlist(
        @RequestParam(name = "userId", required = true) userId: String,
        @RequestParam(name = "itemId", required = true) itemId: Int,
    ): ResponseEntity<Message> {
        return withContext(Dispatchers.IO) {
            val status = produtoService.addToWishlist(userId, itemId)

            if (status == 0) {
                return@withContext ResponseEntity(
                    Message(
                        message = "O Item ja existe na sua lista de desejos", status = HttpStatus
                            .BAD_REQUEST.name
                    ),
                    HttpStatus
                        .BAD_REQUEST
                )
            }
            if (status == 1) {
                return@withContext ResponseEntity(
                    Message(
                        message = "Item adicionado na lista de desejos",
                        status = HttpStatus.CREATED.name
                    ),
                    HttpStatus
                        .CREATED
                )
            }
            ResponseEntity(
                Message(
                    message = "Erro ao adicionar o item na lista de desejos",
                    status = HttpStatus.NOT_ACCEPTABLE.name
                ),
                HttpStatus
                    .NOT_ACCEPTABLE
            )
        }
    }

    @DeleteMapping("/wishlist")
    suspend fun removeFromWishlist(
        @RequestParam(name = "userId", required = true) userId: String,
        @RequestParam(name = "itemId", required = true) itemId: Int,
    ): ResponseEntity<Message> {
        return withContext(Dispatchers.IO) {
            val status = produtoService.removeFromWishlist(userId, itemId)

            if (status == 0) {
                return@withContext ResponseEntity(
                    Message(
                        message = "Item nao encontrado na lista de desejos",
                        HttpStatus.BAD_REQUEST.name
                    ), HttpStatus.BAD_REQUEST
                )
            }
            if (status == 1) {
                return@withContext ResponseEntity(Message(message = "Item removido na lista de desejos"), HttpStatus.OK)
            }

            ResponseEntity(
                Message(
                    message = "Erro ao adicionar ao remover o item na lista de desejos",
                    status = HttpStatus.NOT_FOUND.name
                ),
                HttpStatus
                    .NOT_FOUND
            )

        }
    }

    @GetMapping("/wishlist")
    suspend fun findItemsInWishlist(
        @RequestParam(name = "userId", required = true) id: String,
    ) = withContext(Dispatchers.IO) {
        produtoService.findItemsInWishlist(userId = id)
    }


    @PostMapping("/cart")
    suspend fun addToCart(
        @RequestParam(name = "userId", required = true) userId: String,
        @RequestParam(name = "itemId", required = true) itemId: Int,
    ): ResponseEntity<Message> {
        return withContext(Dispatchers.IO) {
            val status = produtoService.addToCart(userId, itemId)

            if (status == 0) {
                return@withContext ResponseEntity(
                    Message(message = "O Item ja existe no seu shopping cart", status = HttpStatus.BAD_REQUEST.name),
                    HttpStatus.BAD_REQUEST
                )
            }

            if (status == 1) {
                return@withContext ResponseEntity(
                    Message(message = "Item adicionado no shopping cart", status = HttpStatus.CREATED.name),
                    HttpStatus.CREATED
                )
            }

            ResponseEntity(
                Message(
                    message = "Erro ao adicionar ao adicionar o item no shopping cart",
                    status = HttpStatus.NOT_FOUND.name
                ),
                HttpStatus
                    .NOT_FOUND
            )
        }
    }

    @DeleteMapping("/cart")
    suspend fun removeFromCart(
        @RequestParam(name = "userId", required = true) userId: String,
        @RequestParam(name = "itemId", required = true) itemId: Int,
    ): ResponseEntity<Message> {
        return withContext(Dispatchers.IO) {
            val status = produtoService.removeFromCart(userId, itemId)

            if (status == 0) {
                return@withContext ResponseEntity(
                    Message(
                        message = "O Item nao existe",
                        status = HttpStatus.NOT_FOUND.name
                    ),
                    HttpStatus.NOT_FOUND
                )
            }
            if (status == 1) {
                return@withContext ResponseEntity(Message(message = "Item removido do shopping cart"), HttpStatus.OK)
            }

            ResponseEntity(
                Message(
                    message = "Erro ao remover o item do shopping cart",
                    status = HttpStatus.NOT_FOUND.name
                ),
                HttpStatus
                    .NOT_FOUND
            )

        }
    }

    @GetMapping("/cart")
    suspend fun findItemsInCart(
        @RequestParam(name = "userId", required = true) id: String,
    ): Flow<ProdutoDTO> {
        return withContext(Dispatchers.IO) {
            produtoService.findItemsInCart(userId = id)
        }
    }

    @GetMapping("/search")
    suspend fun findItemByName(
        @RequestParam(name = "name", required = true) name: String,
    ) = withContext(Dispatchers.IO) {
        produtoService.search(name = name)
    }

}
