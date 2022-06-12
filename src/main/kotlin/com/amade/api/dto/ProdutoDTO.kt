package com.amade.api.dto

import com.amade.api.model.food.Categoria
import com.amade.api.model.food.Produto
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

data class ProdutoDTO(
    val produto: Produto,
    val categoria: Categoria,
    val images: List<String>,
)
