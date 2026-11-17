package br.pucpr.restaurante.pedidos

data class ItemPedidoResponse(
    val produtoId: Long,
    val produtoNome: String,
    val quantidade: Int,
    val precoUnitario: Double,
    val subtotal: Double
) {
    constructor(item: ItemPedido) : this(
        produtoId = item.produto!!.id!!,
        produtoNome = item.produto!!.nome,
        quantidade = item.quantidade,
        precoUnitario = item.precoUnitario,
        subtotal = item.quantidade * item.precoUnitario
    )
}