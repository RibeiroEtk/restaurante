package br.pucpr.restaurante.produtos

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/produtos")
class ProdutoController(
    private val service: ProdutoService
) {

    @GetMapping
    fun listar(
        @RequestParam(required = false) categoria: String?,
        @RequestParam(required = false) precoMax: Double?,
        @RequestParam(required = false) nome: String?,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(required = false) sortDir: String?
    ): List<ProdutoResponse> {
        val produtos = service.listar(categoria, precoMax, nome, sortBy, sortDir)
        return produtos.map { ProdutoResponse(it) }
    }

    @GetMapping("/{id}")
    fun buscarPorId(@PathVariable id: Long): ProdutoResponse {
        val produto = service.buscarPorId(id)
        return ProdutoResponse(produto)
    }

    @PostMapping
    fun criar(@Valid @RequestBody request: ProdutoRequest): ResponseEntity<ProdutoResponse> {
        val produto = service.criar(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(ProdutoResponse(produto))
    }

    @PutMapping("/{id}")
    fun atualizar(
        @PathVariable id: Long,
        @Valid @RequestBody request: ProdutoRequest
    ): ProdutoResponse {
        val produto = service.atualizar(id, request)
        return ProdutoResponse(produto)
    }

    @DeleteMapping("/{id}")
    fun excluir(@PathVariable id: Long): ResponseEntity<Void> {
        service.excluir(id)
        return ResponseEntity.ok().build()
    }
}