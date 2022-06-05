package com.amade.api

import com.amade.api.model.Usuario
import com.amade.api.service.UsuarioService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class UsuarioController(private val usuarioService: UsuarioService) {

    @GetMapping("/login")
    suspend fun login(
        @RequestParam("email", required = true) email: String,
        @RequestParam("senha", required = true) senha: String,
    ): ResponseEntity<Any> {
        val usuario = usuarioService.loginByEmail(email, senha)
            ?: return ResponseEntity("Dados Invalidos", HttpStatus.BAD_REQUEST)
        return ResponseEntity(usuario, HttpStatus.OK)
    }

    @PostMapping("/register")
    suspend fun register(@RequestBody usuario: Usuario): ResponseEntity<Any> {
        val register = usuarioService.register(usuario)
            ?: return ResponseEntity("Dados invalidos.", HttpStatus.BAD_REQUEST)
        return ResponseEntity(register, HttpStatus.CREATED)
    }

}