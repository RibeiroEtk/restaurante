package br.pucpr.restaurante.usuarios

import org.springframework.data.jpa.repository.JpaRepository

interface UsuarioRepository : JpaRepository<Usuario, Long> {
    fun findByEmailIgnoreCase(email: String): Usuario?
}