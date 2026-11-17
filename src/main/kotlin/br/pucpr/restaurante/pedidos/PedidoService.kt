package br.pucpr.restaurante.pedidos

import br.pucpr.restaurante.erros.BadRequestException
import br.pucpr.restaurante.erros.ForbiddenException
import br.pucpr.restaurante.erros.NotFoundException
import br.pucpr.restaurante.produtos.ProdutoRepository
import br.pucpr.restaurante.usuarios.UsuarioService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PedidoService(
    private val repository: PedidoRepository,
    private val produtoRepository: ProdutoRepository,
    private val usuarioService: UsuarioService
) {
    private val log = LoggerFactory.getLogger(PedidoService::class.java)

    // Os métodos devolvem PedidoResponse (e não a entidade) porque os itens do pedido
    // são carregados sob demanda (lazy). Com open-in-view: false, eles só podem ser lidos
    // dentro da transação, ou seja, aqui no service.

    @Transactional
    fun criar(usuarioId: Long): PedidoResponse {
        val usuario = usuarioService.buscarPorId(usuarioId)
        val pedido = Pedido(usuario = usuario, status = "ABERTO")
        val salvo = repository.save(pedido)
        log.info("Pedido criado. id={}, usuarioId={}", salvo.id, usuarioId)
        return PedidoResponse(salvo)
    }

    @Transactional(readOnly = true)
    fun listar(usuarioId: Long, admin: Boolean): List<PedidoResponse> {
        // O ADMIN vê todos os pedidos; o cliente vê só os seus
        val pedidos = if (admin) {
            repository.findAllByOrderByIdDesc()
        } else {
            repository.findByUsuarioIdOrderByIdDesc(usuarioId)
        }
        return pedidos.map { PedidoResponse(it) }
    }

    @Transactional(readOnly = true)
    fun buscar(id: Long, usuarioId: Long, admin: Boolean): PedidoResponse {
        val pedido = buscarPedidoPermitido(id, usuarioId, admin)
        return PedidoResponse(pedido)
    }

    @Transactional
    fun adicionarItem(id: Long, request: ItemPedidoRequest, usuarioId: Long, admin: Boolean): PedidoResponse {
        val pedido = buscarPedidoPermitido(id, usuarioId, admin)
        verificarAberto(pedido)

        val produtoId = request.produtoId!!
        val produto = produtoRepository.findById(produtoId).orElse(null)
        if (produto == null) {
            log.warn("Tentativa de adicionar produto inexistente. pedidoId={}, produtoId={}", id, produtoId)
            throw BadRequestException("Produto com id $produtoId não existe")
        }

        // Se o produto já está no pedido, só aumenta a quantidade
        val existente = procurarItem(pedido, produtoId)
        if (existente != null) {
            existente.quantidade = existente.quantidade + request.quantidade!!
            log.info("Quantidade atualizada. pedidoId={}, produtoId={}, quantidade={}", id, produtoId, existente.quantidade)
        } else {
            val item = ItemPedido(
                pedido = pedido,
                produto = produto,
                quantidade = request.quantidade!!,
                precoUnitario = produto.preco  // preço "congelado" no momento da compra
            )
            pedido.itens.add(item)
            log.info("Item adicionado. pedidoId={}, produtoId={}, quantidade={}", id, produtoId, item.quantidade)
        }

        val salvo = repository.save(pedido)  // o cascade salva os itens junto
        return PedidoResponse(salvo)
    }

    @Transactional
    fun removerItem(id: Long, produtoId: Long, usuarioId: Long, admin: Boolean): PedidoResponse {
        val pedido = buscarPedidoPermitido(id, usuarioId, admin)
        verificarAberto(pedido)

        val item = procurarItem(pedido, produtoId)
        if (item == null) {
            log.warn("Produto não está no pedido. pedidoId={}, produtoId={}", id, produtoId)
            throw NotFoundException("O produto $produtoId não está no pedido $id")
        }

        // Com orphanRemoval = true, tirar o item da lista já apaga a linha no banco
        pedido.itens.remove(item)
        val salvo = repository.save(pedido)
        log.info("Item removido. pedidoId={}, produtoId={}", id, produtoId)
        return PedidoResponse(salvo)
    }

    @Transactional
    fun fechar(id: Long, usuarioId: Long, admin: Boolean): PedidoResponse {
        val pedido = buscarPedidoPermitido(id, usuarioId, admin)
        verificarAberto(pedido)

        if (pedido.itens.isEmpty()) {
            throw BadRequestException("Não é possível fechar um pedido sem itens")
        }

        pedido.status = "FECHADO"
        val salvo = repository.save(pedido)
        log.info("Pedido fechado. id={}", id)
        return PedidoResponse(salvo)
    }

    @Transactional
    fun excluir(id: Long, usuarioId: Long, admin: Boolean) {
        val pedido = buscarPedidoPermitido(id, usuarioId, admin)
        repository.delete(pedido)  // o cascade apaga os itens junto
        log.info("Pedido excluído. id={}, por usuarioId={}", id, usuarioId)
    }

    // ---------- Funções auxiliares ----------

    // Busca o pedido e confere se o usuário pode mexer nele (dono ou ADMIN)
    private fun buscarPedidoPermitido(id: Long, usuarioId: Long, admin: Boolean): Pedido {
        val pedido = repository.findById(id).orElse(null)
        if (pedido == null) {
            log.warn("Pedido não encontrado. id={}", id)
            throw NotFoundException("Pedido com id $id não encontrado")
        }

        if (!admin && pedido.usuario!!.id != usuarioId) {
            log.warn("Acesso negado ao pedido. pedidoId={}, usuarioId={}", id, usuarioId)
            throw ForbiddenException("Este pedido pertence a outro usuário")
        }
        return pedido
    }

    private fun verificarAberto(pedido: Pedido) {
        if (pedido.status != "ABERTO") {
            throw BadRequestException("O pedido ${pedido.id} já está ${pedido.status} e não pode ser alterado")
        }
    }

    private fun procurarItem(pedido: Pedido, produtoId: Long): ItemPedido? {
        for (item in pedido.itens) {
            if (item.produto!!.id == produtoId) {
                return item
            }
        }
        return null
    }
}