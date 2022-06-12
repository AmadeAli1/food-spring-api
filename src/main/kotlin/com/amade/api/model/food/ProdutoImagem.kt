package com.amade.api.model.food

import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Table("produto_imagem")
data class ProdutoImagem(
    @field:NotNull @field:NotBlank @Column("produto_id") var produtoId: Int,
    @field:NotNull @field:NotBlank @Column("imagem") var image: String,
)