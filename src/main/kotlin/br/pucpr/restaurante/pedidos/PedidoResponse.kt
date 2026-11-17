package br.pucpr.restaurante.pedidos

import java.time.LocalDateTime

data class PedidoResponse(
    val id: Long,
    val dataCriacao: LocalDateTime,
    val status: String,
    val usuarioId: Long,
    val usuarioNome: String,
    val itens: List<ItemPedidoResponse>
) {
    // Total do pedido: soma dos subtotais dos itens
    val total: Double

    init {
        var soma = 0.0
        for (item in itens) {
            soma += item.subtotal
        }
        total = soma
    }

    constructor(pedido: Pedido) : this(
        id = pedido.id!!,
        dataCriacao = pedido.dataCriacao,
        status = pedido.status,
        usuarioId = pedido.usuario!!.id!!,
        usuarioNome = pedido.usuario!!.nome,
        itens = pedido.itens.map { ItemPedidoResponse(it) }
    )
}