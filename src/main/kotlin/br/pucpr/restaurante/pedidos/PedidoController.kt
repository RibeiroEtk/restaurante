package br.pucpr.restaurante.pedidos

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/pedidos")
class PedidoController(
    private val service: PedidoService
) {

    @PostMapping
    fun criar(authentication: Authentication): ResponseEntity<PedidoResponse> {
        val pedido = service.criar(usuarioId(authentication))
        return ResponseEntity.status(HttpStatus.CREATED).body(pedido)
    }

    @GetMapping
    fun listar(authentication: Authentication): List<PedidoResponse> {
        return service.listar(usuarioId(authentication), isAdmin(authentication))
    }

    @GetMapping("/{id}")
    fun buscar(@PathVariable id: Long, authentication: Authentication): PedidoResponse {
        return service.buscar(id, usuarioId(authentication), isAdmin(authentication))
    }

    // Associação Pedido ↔ Produto: adicionar produto ao pedido
    @PostMapping("/{id}/itens")
    fun adicionarItem(
        @PathVariable id: Long,
        @Valid @RequestBody request: ItemPedidoRequest,
        authentication: Authentication
    ): PedidoResponse {
        return service.adicionarItem(id, request, usuarioId(authentication), isAdmin(authentication))
    }

    // Associação Pedido ↔ Produto: remover produto do pedido
    @DeleteMapping("/{id}/itens/{produtoId}")
    fun removerItem(
        @PathVariable id: Long,
        @PathVariable produtoId: Long,
        authentication: Authentication
    ): PedidoResponse {
        return service.removerItem(id, produtoId, usuarioId(authentication), isAdmin(authentication))
    }

    @PutMapping("/{id}/fechar")
    fun fechar(@PathVariable id: Long, authentication: Authentication): PedidoResponse {
        return service.fechar(id, usuarioId(authentication), isAdmin(authentication))
    }

    @DeleteMapping("/{id}")
    fun excluir(@PathVariable id: Long, authentication: Authentication): ResponseEntity<Void> {
        service.excluir(id, usuarioId(authentication), isAdmin(authentication))
        return ResponseEntity.ok().build()
    }

    // ---------- Funções auxiliares: dados do usuário logado (vindos do token) ----------

    private fun usuarioId(authentication: Authentication): Long {
        return authentication.principal as Long
    }

    private fun isAdmin(authentication: Authentication): Boolean {
        for (autoridade in authentication.authorities) {
            if (autoridade.authority == "ROLE_ADMIN") {
                return true
            }
        }
        return false
    }
}