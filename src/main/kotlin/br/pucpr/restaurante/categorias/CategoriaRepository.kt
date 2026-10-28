package br.pucpr.restaurante.categorias

import org.springframework.data.jpa.repository.JpaRepository

interface CategoriaRepository : JpaRepository<Categoria, Long> {
    fun findByNomeIgnoreCase(nome: String): Categoria?
}