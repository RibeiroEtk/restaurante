package br.pucpr.restaurante.pedidos

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class ItemPedidoRequest(
    @field:NotNull(message = "O produto é obrigatório")
    val produtoId: Long?,

    @field:NotNull(message = "A quantidade é obrigatória")
    @field:Positive(message = "A quantidade deve ser maior que zero")
    @field:Max(value = 50, message = "A quantidade máxima por item é 50")
    val quantidade: Int?
)