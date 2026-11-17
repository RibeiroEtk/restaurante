package br.pucpr.restaurante.usuarios

data class UsuarioResponse(
    val id: Long,
    val nome: String,
    val email: String,
    val papel: String
) {
    constructor(usuario: Usuario) : this(
        id = usuario.id!!,
        nome = usuario.nome,
        email = usuario.email,
        papel = usuario.papel
    )
}