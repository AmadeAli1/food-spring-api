package com.amade.api.service.food

import com.amade.api.dto.Page
import com.amade.api.dto.ProdutoDTO
import com.amade.api.exception.ApiException
import com.amade.api.model.food.Produto
import com.amade.api.repository.ProdutoRepository
import com.amade.api.service.ImageService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service

@Service
class ProdutoService(
    private val categoriaService: CategoriaService,
    private val imageService: ImageService,
    private val repository: ProdutoRepository,
) {

    suspend fun save(files: MutableList<FilePart>, request: Produto) = withContext(Dispatchers.IO) {
        val produto = repository.save(entity = request)
        val categoria = categoriaService.findCategory(request.category)
        val images = files.map {
            val imageUrl = imageService.save(it)!!
            try {
                val status = repository.inserir_imagem(itemId = produto.id, image = imageUrl)
                if (status != 1) {
                    throw ApiException("Imagem ${it.filename()} nao gravada!")
                }
                imageUrl
            } catch (e: Exception) {
                throw ApiException(e.message!!)
            }
        }.onEach {
            println(it)
        }
        ProdutoDTO(produto = produto, categoria = categoria!!, images = images)
    }

    suspend fun findAll(): Flow<ProdutoDTO> {
        val xs = repository.findAll()
        return withContext(Dispatchers.IO) {
            xs.map { p -> mapper(p) }
        }
    }

    private suspend fun mapper(produto: Produto): ProdutoDTO {
        val ctg = categoriaService.findCategory(produto.category)
        val img = repository.obter_todas_imagens(produto.id)
        val images = img.map { it.image }.toList()
        return ProdutoDTO(produto = produto, categoria = ctg!!, images = images)
    }

    suspend fun pagination(page: Int) = withContext(Dispatchers.IO) {
        val limit: Double = 20.0
        val count = repository.count()
        val paginas = count.div(limit).plus(1).toInt()
        val start = if (page == 0) {
            0
        } else {
            page * limit.toInt()
        }

        val data = repository.findAll(start = start).map { p -> mapper(p) }.toList()

        val next = if ((data.size == 20).and(count > start * 20)) {
            "http://localhost:8080/api/food/produto?page=${page + 1}"
        } else {
            null
        }
        Page<ProdutoDTO>(
            data = data,
            pageSize = data.size,
            pageNumber = page,
            totalPages = paginas,
            totalItems = count,
            maxPageSize = 20,
            next = next
        )
    }

    suspend fun addToWishlist(userId: String, itemId: Int): Int {
        val exist = existsWishlist(userId, itemId)
        return if (!exist) {
            try {
                repository.adicionar_na_lista_de_desejos(itemId = itemId, userId = userId)
            } catch (e: Exception) {
                -1
            }
        } else {
            0
        }
    }

    suspend fun removeFromWishlist(userId: String, itemId: Int): Int {
        val exist = existsWishlist(userId, itemId)
        return if (exist) {
            try {
                repository.remover_da_lista_de_desejos(itemId = itemId, userId = userId)
            } catch (e: Exception) {
                -1
            }
        } else {
            0
        }
    }

    suspend fun findItemsInWishlist(userId: String): Flow<ProdutoDTO> {
        return try {
            val items = repository.obter_todos_items_da_lista_de_desejos_do_usuario(userId = userId)
            items.map { mapper(it) }
        } catch (e: Exception) {
            throw ApiException(e.message!!)
        }
    }


    suspend fun addToCart(userId: String, itemId: Int): Int {
        val exist = existsCart(userId, itemId)
        return if (!exist) {
            try {
                repository.adicionar_no_carrinho_de_compras(itemId = itemId, userId = userId)
            } catch (e: Exception) {
                -1
            }
        } else {
            0
        }
    }

    suspend fun removeFromCart(userId: String, itemId: Int): Int {
        val exist = existsCart(userId, itemId)
        return if (!exist) {
            try {
                repository.remover_do_carrinho_de_compras(itemId = itemId, userId = userId)
            } catch (e: Exception) {
                -1
            }
        } else {
            0
        }
    }

    suspend fun findItemsInCart(userId: String): Flow<ProdutoDTO> {
        return try {
            val items = repository.obter_todos_items_do_usuario(userId = userId)
            items.map { mapper(it) }
        } catch (e: Exception) {
            throw RuntimeException(e.message)
        }
    }

    suspend fun search(name: String): Flow<ProdutoDTO> {
        return withContext(Dispatchers.IO) {
            withContext(Dispatchers.Default) {
                repository.pesquisar(query = name).map { mapper(it) }
            }
        }
    }

    suspend fun existsWishlist(userId: String, itemId: Int): Boolean {
        return repository.verificar_existencia_do_produto_na_lista_de_desejos(userId, itemId)
    }

    suspend fun existsCart(userId: String, itemId: Int): Boolean {
        return repository.verificar_existencia_do_produto_no_carrinho_de_compras(userId, itemId)
    }

    /**
     * suspend fun addLike(userId: String, itemId: Int): Int {
    return itemRepository.adicionar_like_ao_item(itemId).let {
    if (it == 0) {
    return@let 0
    }
    itemRepository.adicionar_like_do_usuario(userId, itemId)
    }
    }

    suspend fun removeLike(userId: String, itemId: Int): Int {
    return repository.remover_like_do_item(itemId).let {
    if (it == 0) {
    return@let 0
    }
    itemRepository.remover_like_do_usuario(userId, itemId)
    }
    }

    suspend fun verificar_existencia_do_like(
    userId: String, itemId: Int,
    ): Boolean {
    return repository.verificar_existencia_de_like_do_usuario(userId, itemId)
    }
     */

}