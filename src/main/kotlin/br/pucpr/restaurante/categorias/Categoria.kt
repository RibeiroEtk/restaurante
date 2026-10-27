package br.pucpr.restaurante.categorias

import br.pucpr.restaurante.produtos.Produto
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "categorias")
class Categoria(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, unique = true)
    var nome: String = "",

    @Column(nullable = false)
    var descricao: String = "",

    @OneToMany(mappedBy = "categoria")
    var produtos: MutableList<Produto> = mutableListOf()
)