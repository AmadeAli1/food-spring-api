package com.amade.api.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import javax.validation.constraints.NotNull

@Table("image")
data class Image(
    @field:NotNull @Column("image") val image: ByteArray,
    @field:NotNull @Column("filetype") val type: String,
    @field:NotNull @Column("filename") val name: String,
) {
    @Id
    @Column("id")
    var id: Int = 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Image

        if (!image.contentEquals(other.image)) return false

        return true
    }

    override fun hashCode(): Int {
        return image.contentHashCode()
    }


}