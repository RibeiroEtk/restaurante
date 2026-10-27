package br.pucpr.restaurante.produtos

import br.pucpr.restaurante.categorias.Categoria
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "produtos")
class Produto(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var nome: String = "",

    @Column(nullable = false)
    var descricao: String = "",

    @Column(nullable = false)
    var preco: Double = 0.0,

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    var categoria: Categoria? = null
)