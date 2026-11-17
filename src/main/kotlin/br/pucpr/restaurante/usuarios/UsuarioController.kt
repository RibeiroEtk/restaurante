package br.pucpr.restaurante.usuarios

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/usuarios")
class UsuarioController(
    private val service: UsuarioService
) {

    @PostMapping
    fun cadastrar(@Valid @RequestBody request: UsuarioRequest): ResponseEntity<UsuarioResponse> {
        val usuario = service.cadastrar(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioResponse(usuario))
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): LoginResponse {
        return service.login(request)
    }

    // Mostra quem está logado (lido do token)
    @GetMapping("/me")
    fun me(authentication: Authentication): UsuarioResponse {
        val usuarioId = authentication.principal as Long
        val usuario = service.buscarPorId(usuarioId)
        return UsuarioResponse(usuario)
    }
}