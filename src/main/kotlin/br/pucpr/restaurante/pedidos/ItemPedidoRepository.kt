package br.pucpr.restaurante.pedidos

import org.springframework.data.jpa.repository.JpaRepository

interface ItemPedidoRepository : JpaRepository<ItemPedido, Long> {
    fun existsByProdutoId(produtoId: Long): Boolean
}