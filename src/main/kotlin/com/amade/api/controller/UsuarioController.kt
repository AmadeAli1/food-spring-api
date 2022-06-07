package com.amade.api.controller

import com.amade.api.model.Usuario
import com.amade.api.service.TokenService
import com.amade.api.service.UsuarioService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api")
class UsuarioController(
    private val usuarioService: UsuarioService,
    private val tokenService: TokenService,
) {

    @GetMapping("/login")
    suspend fun login(
        @RequestParam("email", required = true) email: String,
        @RequestParam("senha", required = true) senha: String,
    ): ResponseEntity<Any> {
        val usuario = usuarioService.loginByEmail(email, senha)
        if (usuario != null) {
            return ResponseEntity(usuario, HttpStatus.OK)
        }
        return ResponseEntity("Verifique seu Email e Senha", HttpStatus.BAD_REQUEST)

    }

    @PostMapping("/register")
    suspend fun register(@Valid @RequestBody usuario: Usuario): ResponseEntity<Any> {
        val register = usuarioService.register(usuario)
        if (register != null) {
            return ResponseEntity(register, HttpStatus.CREATED)
        }
        return ResponseEntity("Dados invalidos.", HttpStatus.BAD_REQUEST)
    }

    @GetMapping("/confirmAccount")
    suspend fun confirmAccount(@RequestParam("token", required = true) token: String): ResponseEntity<Any> {
        return withContext(Dispatchers.IO) {
            val confirmAccount = tokenService.confirmAccount(id = token)
            if (confirmAccount != null) {
                return@withContext ResponseEntity(confirmAccount, HttpStatus.OK)
            }
            return@withContext ResponseEntity("Ocorreu um erro ao confirmar o Token", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

}