package com.amade.api.model.food

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Table("categoria")
data class Categoria(
    @field:NotNull @field:NotBlank @Column("nome") val name: String,
    @field:NotNull @field:NotBlank @Column("imagem") var image: String,
) {
    @Id
    var id: Int = 0

    data class CategoriaRequest(
        @field:NotNull @field:NotBlank val name: String,
    )
}
