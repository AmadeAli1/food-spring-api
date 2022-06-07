package com.amade.api.utils

import org.springframework.beans.factory.annotation.Value

object Constantes {
    @Value(value = "\${source.food.api.token}")
    val confirmTokenUrl: String? = null
}