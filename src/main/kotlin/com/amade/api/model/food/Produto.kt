package com.amade.api.model.food

import org.hibernate.validator.constraints.Length
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.sql.Date
import java.time.LocalDateTime
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull


@Table("produto")
data class Produto(
    @field:NotNull @field:NotBlank @field:Length(max = 50, min = 5) @Column("nome") val name: String,
    @field:NotNull @field:Min(1) @Column("preco") var price: Float,
    @field:NotNull @field:Min(1) @Column("quantidade") var quantity: Int,
    @field:NotNull @Column("category_id") var category: Int,
    @Column("likes") var likes: Int = 0,
    @Column("createdAt") var date: LocalDateTime? = LocalDateTime.now(),
) {
    @Id
    @Column("id")
    var id: Int = 0

    constructor() : this("", 0.0f, 0, 0, 0, LocalDateTime.now())
}