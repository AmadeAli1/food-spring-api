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
}