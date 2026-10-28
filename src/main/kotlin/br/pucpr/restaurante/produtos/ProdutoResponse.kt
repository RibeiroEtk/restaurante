package br.pucpr.restaurante.produtos

data class ProdutoResponse(
    val id: Long,
    val nome: String,
    val descricao: String,
    val preco: Double,
    val categoriaId: Long,
    val categoriaNome: String
) {
    constructor(produto: Produto) : this(
        id = produto.id!!,
        nome = produto.nome,
        descricao = produto.descricao,
        preco = produto.preco,
        categoriaId = produto.categoria!!.id!!,
        categoriaNome = produto.categoria!!.nome
    )
}