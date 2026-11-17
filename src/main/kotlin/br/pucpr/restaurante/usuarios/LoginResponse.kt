package br.pucpr.restaurante.usuarios

data class LoginResponse(
    val token: String,
    val usuario: UsuarioResponse
)