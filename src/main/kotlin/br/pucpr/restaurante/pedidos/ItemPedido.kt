package br.pucpr.restaurante.pedidos

import br.pucpr.restaurante.produtos.Produto
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "itens_pedido",
    uniqueConstraints = [UniqueConstraint(columnNames = ["pedido_id", "produto_id"])]
)
class ItemPedido(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    var pedido: Pedido? = null,

    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    var produto: Produto? = null,

    @Column(nullable = false)
    var quantidade: Int = 1,

    @Column(nullable = false)
    var precoUnitario: Double = 0.0
)