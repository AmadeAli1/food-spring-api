package com.amade.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FoodApiApplication

fun main(args: Array<String>) {
    runApplication<FoodApiApplication>(*args)
}

