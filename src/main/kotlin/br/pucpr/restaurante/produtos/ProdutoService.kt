package br.pucpr.restaurante.produtos

import br.pucpr.restaurante.categorias.Categoria
import br.pucpr.restaurante.categorias.CategoriaRepository
import br.pucpr.restaurante.erros.BadRequestException
import br.pucpr.restaurante.erros.NotFoundException
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProdutoService(
    private val repository: ProdutoRepository,
    private val categoriaRepository: CategoriaRepository
) {
    private val log = LoggerFactory.getLogger(ProdutoService::class.java)

    fun listar(
        categoria: String?,
        precoMax: Double?,
        nome: String?,
        sortBy: String?,
        sortDir: String?
    ): List<Produto> {
        // Campo de ordenação: só aceita "nome" ou "preco". Padrão: nome
        val campo = if (sortBy == null) "nome" else sortBy.lowercase()
        if (campo != "nome" && campo != "preco") {
            throw BadRequestException("Valor inválido para sortBy: '$sortBy'. Use 'nome' ou 'preco'")
        }

        // Direção: só aceita ASC ou DESC (sem diferenciar maiúsculas). Padrão: ASC
        val direcaoTexto = if (sortDir == null) "ASC" else sortDir.uppercase()
        val direcao = when (direcaoTexto) {
            "ASC" -> Sort.Direction.ASC
            "DESC" -> Sort.Direction.DESC
            else -> throw BadRequestException("Valor inválido para sortDir: '$sortDir'. Use 'ASC' ou 'DESC'")
        }

        if (precoMax != null && precoMax < 0) {
            throw BadRequestException("O precoMax não pode ser negativo")
        }

        log.info(
            "Listando produtos. categoria={}, precoMax={}, nome={}, sortBy={}, sortDir={}",
            categoria, precoMax, nome, campo, direcaoTexto
        )

        val ordenacao = Sort.by(direcao, campo)
        return repository.buscarComFiltros(categoria, precoMax, nome, ordenacao)
    }

    fun buscarPorId(id: Long): Produto {
        val produto = repository.findById(id).orElse(null)
        if (produto == null) {
            log.warn("Produto não encontrado. id={}", id)
            throw NotFoundException("Produto com id $id não encontrado")
        }
        return produto
    }

    fun criar(request: ProdutoRequest): Produto {
        val categoria = buscarCategoria(request.categoriaId!!)

        val produto = Produto(
            nome = request.nome!!.trim(),
            descricao = request.descricao!!.trim(),
            preco = request.preco!!,
            categoria = categoria
        )
        val salvo = repository.save(produto)
        log.info("Produto criado. id={}, nome={}, categoria={}", salvo.id, salvo.nome, categoria.nome)
        return salvo
    }

    @Transactional
    fun atualizar(id: Long, request: ProdutoRequest): Produto {
        val produto = buscarPorId(id)
        val categoria = buscarCategoria(request.categoriaId!!)

        produto.nome = request.nome!!.trim()
        produto.descricao = request.descricao!!.trim()
        produto.preco = request.preco!!
        produto.categoria = categoria

        val salvo = repository.save(produto)
        log.info("Produto atualizado. id={}, nome={}", salvo.id, salvo.nome)
        return salvo
    }

    @Transactional
    fun excluir(id: Long) {
        val produto = buscarPorId(id)
        repository.delete(produto)
        log.info("Produto excluído. id={}", id)
    }

    // Função auxiliar: busca a categoria ou dá erro 400
    private fun buscarCategoria(categoriaId: Long): Categoria {
        val categoria = categoriaRepository.findById(categoriaId).orElse(null)
        if (categoria == null) {
            log.warn("Tentativa de usar categoria inexistente. categoriaId={}", categoriaId)
            throw BadRequestException("Categoria com id $categoriaId não existe")
        }
        return categoria
    }
}