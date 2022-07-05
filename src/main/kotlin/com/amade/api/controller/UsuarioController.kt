package com.amade.api.controller

import com.amade.api.dto.UsuarioDTO
import com.amade.api.model.Usuario
import com.amade.api.service.ImageService
import com.amade.api.service.TokenService
import com.amade.api.service.UsuarioService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/user")
class UsuarioController private constructor(
    private val usuarioService: UsuarioService,
    private val tokenService: TokenService,
    private val imageService: ImageService,
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
        return ResponseEntity("Check the data entered", HttpStatus.BAD_REQUEST)

    }

    @PostMapping("/register")
    suspend fun register(@Valid @RequestBody usuario: Usuario): ResponseEntity<Any> {
        val register = usuarioService.register(usuario)
        if (register != null) {
            return ResponseEntity(register, HttpStatus.CREATED)
        }
        return ResponseEntity("Invalid data for account registration", HttpStatus.BAD_REQUEST)
    }

    @GetMapping("/confirm")
    suspend fun confirmAccount(@RequestParam("token", required = true) token: String): ResponseEntity<Any> {
        return withContext(Dispatchers.IO) {
            val confirmAccount = tokenService.confirmAccount(id = token)
            if (confirmAccount != null) {
                return@withContext ResponseEntity(confirmAccount, HttpStatus.OK)
            }
            return@withContext ResponseEntity(
                "An error occurred while confirming the Token",
                HttpStatus.NOT_ACCEPTABLE
            )
        }
    }

    @PostMapping(
        "/profile",
        consumes = [
            MediaType.MULTIPART_FORM_DATA_VALUE,
            MediaType.IMAGE_PNG_VALUE,
            MediaType.IMAGE_JPEG_VALUE,
            MediaType.APPLICATION_OCTET_STREAM_VALUE,
            MediaType.APPLICATION_JSON_VALUE

        ]
    )
    suspend fun saveProfileImage(
        @RequestPart("file") file: FilePart,
        @RequestParam("userId", required = true) id: String,
    ): ResponseEntity<UsuarioDTO>? = withContext(Dispatchers.IO) {
        val linkDownload = imageService.save(file)
        val user = usuarioService.addProfilePicture(usuarioId = id, linkDownload)
        ResponseEntity(user, HttpStatus.CREATED)
    }

    @DeleteMapping(
        "/profile"
    )
    suspend fun removeProfileImage(@RequestParam("userId", required = true) id: String): ResponseEntity<UsuarioDTO?> {
        val user = usuarioService.deleteProfilePicture(usuarioId = id)
        return ResponseEntity(user, HttpStatus.OK)
    }

    @GetMapping("/all")
    suspend fun findAll() = usuarioService.findAllUsers()

}