package br.pucpr.restaurante.usuarios

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UsuarioRequest(
    @field:NotBlank(message = "O nome é obrigatório")
    @field:Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
    val nome: String?,

    @field:NotBlank(message = "O e-mail é obrigatório")
    @field:Email(message = "E-mail inválido")
    val email: String?,

    @field:NotBlank(message = "A senha é obrigatória")
    @field:Size(min = 6, max = 50, message = "A senha deve ter entre 6 e 50 caracteres")
    val senha: String?
)