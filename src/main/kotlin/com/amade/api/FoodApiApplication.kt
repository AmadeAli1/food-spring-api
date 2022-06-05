package com.amade.api

import com.amade.api.model.Usuario
import com.amade.api.service.UsuarioService
import kotlinx.coroutines.runBlocking
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.util.*

@SpringBootApplication
class FoodApiApplication(val service: UsuarioService) : CommandLineRunner {

    override fun run(vararg args: String?) {

        runBlocking {
//            val user = Usuario(
//                uid = UUID.randomUUID().toString(),
//                name = "Amade Ali",
//                senha = "amade",
//                email = "amadeali@gmail.com"
//            )
//            val usuario = service.register(user)
//            println(usuario)
        }

    }
}

fun main(args: Array<String>) {
    runApplication<FoodApiApplication>(*args)
}

