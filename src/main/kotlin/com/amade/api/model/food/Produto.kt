package com.amade.api.model.food

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.validator.constraints.Length
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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
    @field:NotNull @field:NotBlank @field:Length(max = 255, min = 10) @Column("description") var description: String,
    @field:JsonIgnore
    @Column("createdAt") var date: LocalDateTime? = LocalDateTime.now(),
) {
    @Id
    @Column("id")
    var id: Int = 0

    @org.springframework.data.annotation.Transient
    var createdAt: String? = date!!.format(DateTimeFormatter.ISO_DATE_TIME)

    constructor() : this(
        "", 0.0f, 0,
        0, 0, "", LocalDateTime.now()
    )

}