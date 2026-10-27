package br.pucpr.restaurante.usuarios

import br.pucpr.restaurante.pedidos.Pedido
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "usuarios")
class Usuario(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var nome: String = "",

    @Column(nullable = false, unique = true)
    var email: String = "",

    @Column(nullable = false)
    var senha: String = "",

    @Column(nullable = false)
    var papel: String = "USER",

    @OneToMany(mappedBy = "usuario")
    var pedidos: MutableList<Pedido> = mutableListOf()
)