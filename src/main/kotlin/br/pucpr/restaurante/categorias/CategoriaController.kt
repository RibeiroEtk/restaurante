package br.pucpr.restaurante.categorias

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
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/categorias")
class CategoriaController(
    private val service: CategoriaService
) {

    @GetMapping
    fun listar(): List<CategoriaResponse> {
        val categorias = service.listar()
        return categorias.map { CategoriaResponse(it) }
    }

    @GetMapping("/{id}")
    fun buscarPorId(@PathVariable id: Long): CategoriaResponse {
        val categoria = service.buscarPorId(id)
        return CategoriaResponse(categoria)
    }

    @PostMapping
    fun criar(@Valid @RequestBody request: CategoriaRequest): ResponseEntity<CategoriaResponse> {
        val categoria = service.criar(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(CategoriaResponse(categoria))
    }

    @PutMapping("/{id}")
    fun atualizar(
        @PathVariable id: Long,
        @Valid @RequestBody request: CategoriaRequest
    ): CategoriaResponse {
        val categoria = service.atualizar(id, request)
        return CategoriaResponse(categoria)
    }

    @DeleteMapping("/{id}")
    fun excluir(@PathVariable id: Long): ResponseEntity<Void> {
        service.excluir(id)
        return ResponseEntity.ok().build()
    }
}