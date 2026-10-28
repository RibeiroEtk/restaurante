package br.pucpr.restaurante.produtos

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

data class ProdutoRequest(
    @field:NotBlank(message = "O nome é obrigatório")
    @field:Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
    val nome: String?,

    @field:NotBlank(message = "A descrição é obrigatória")
    @field:Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres")
    val descricao: String?,

    @field:NotNull(message = "O preço é obrigatório")
    @field:Positive(message = "O preço deve ser maior que zero")
    val preco: Double?,

    @field:NotNull(message = "A categoria é obrigatória")
    val categoriaId: Long?
)