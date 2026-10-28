package br.pucpr.restaurante.categorias

import br.pucpr.restaurante.erros.BadRequestException
import br.pucpr.restaurante.erros.NotFoundException
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CategoriaService(
    private val repository: CategoriaRepository
) {
    private val log = LoggerFactory.getLogger(CategoriaService::class.java)

    fun listar(): List<Categoria> {
        return repository.findAll(Sort.by("nome"))
    }

    fun buscarPorId(id: Long): Categoria {
        val categoria = repository.findById(id).orElse(null)
        if (categoria == null) {
            log.warn("Categoria não encontrada. id={}", id)
            throw NotFoundException("Categoria com id $id não encontrada")
        }
        return categoria
    }

    fun criar(request: CategoriaRequest): Categoria {
        val nome = request.nome!!.trim()

        if (repository.findByNomeIgnoreCase(nome) != null) {
            log.warn("Tentativa de criar categoria duplicada. nome={}", nome)
            throw BadRequestException("Já existe uma categoria com o nome '$nome'")
        }

        val categoria = Categoria(
            nome = nome,
            descricao = request.descricao!!.trim()
        )
        val salva = repository.save(categoria)
        log.info("Categoria criada. id={}, nome={}", salva.id, salva.nome)
        return salva
    }

    @Transactional
    fun atualizar(id: Long, request: CategoriaRequest): Categoria {
        val categoria = buscarPorId(id)
        val nome = request.nome!!.trim()

        val outra = repository.findByNomeIgnoreCase(nome)
        if (outra != null && outra.id != categoria.id) {
            log.warn("Tentativa de renomear categoria para nome existente. id={}, nome={}", id, nome)
            throw BadRequestException("Já existe uma categoria com o nome '$nome'")
        }

        categoria.nome = nome
        categoria.descricao = request.descricao!!.trim()
        val salva = repository.save(categoria)
        log.info("Categoria atualizada. id={}, nome={}", salva.id, salva.nome)
        return salva
    }

    @Transactional
    fun excluir(id: Long) {
        val categoria = buscarPorId(id)

        if (categoria.produtos.isNotEmpty()) {
            log.warn("Tentativa de excluir categoria com produtos. id={}", id)
            throw BadRequestException(
                "Não é possível excluir a categoria '${categoria.nome}' porque ela possui produtos"
            )
        }

        repository.delete(categoria)
        log.info("Categoria excluída. id={}", id)
    }
}