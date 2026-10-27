package br.pucpr.restaurante.pedidos

import br.pucpr.restaurante.usuarios.Usuario
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "pedidos")
class Pedido(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var dataCriacao: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var status: String = "ABERTO",

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    var usuario: Usuario? = null,

    @OneToMany(mappedBy = "pedido", cascade = [CascadeType.ALL], orphanRemoval = true)
    var itens: MutableList<ItemPedido> = mutableListOf()
)