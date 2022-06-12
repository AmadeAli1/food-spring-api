package com.amade.api.service.food

import com.amade.api.configurations.UrlConfiguration.Companion.imageUrl
import com.amade.api.exception.ApiException
import com.amade.api.model.food.Categoria
import com.amade.api.repository.CategoriaRepository
import com.amade.api.service.ImageService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service

@Service
class CategoriaService(
    private val repository: CategoriaRepository,
    private val imageService: ImageService,
) {

    suspend fun save(name: String, filePart: FilePart) = withContext(Dispatchers.IO) {
        val imageUrl = imageService.save(file = filePart)!!
        try {
            val categoria = Categoria(name = name, image = imageUrl)
            repository.save(categoria)
        } catch (e: Exception) {
            throw ApiException("Ocorreu um erro ao gravar categoria:: ${e.message}")
        }
    }

    suspend fun findCategory(id: Int) = repository.findById(id)

    suspend fun findAll() = repository.findAll()

    suspend fun delete(id: Int) = withContext(Dispatchers.IO) {
        val categoria = findCategory(id)

        if (categoria == null) {
            throw ApiException("Categoria com id: $id nao existe!")
        } else {
            val ss = categoria.image.drop(imageUrl!!.length)
            val delete = repository.delete(id)
            if (delete==1){
                imageService.deleteById(ss.toInt())
            }
            0
        }
    }

}