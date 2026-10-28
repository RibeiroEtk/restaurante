package br.pucpr.restaurante.categorias

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CategoriaRequest(
    @field:NotBlank(message = "O nome é obrigatório")
    @field:Size(max = 50, message = "O nome deve ter no máximo 50 caracteres")
    val nome: String?,

    @field:NotBlank(message = "A descrição é obrigatória")
    @field:Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres")
    val descricao: String?
)