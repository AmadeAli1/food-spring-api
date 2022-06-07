package com.amade.api

import com.amade.api.service.TokenService
import kotlinx.coroutines.runBlocking
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FoodApiApplication : CommandLineRunner {
    override fun run(vararg args: String?) {
        runBlocking {
//
//            val token = "4674fd26-c711-4e0f-a215-4f01c5263f45"
//            val createToken = service.createToken(token)
//            println(createToken)
        }

    }
}

fun main(args: Array<String>) {
    runApplication<FoodApiApplication>(*args)
}

