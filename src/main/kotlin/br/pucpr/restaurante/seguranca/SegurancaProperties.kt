package br.pucpr.restaurante.seguranca

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "seguranca")
data class SegurancaProperties(
    val jwtSecret: String,
    val jwtExpiracaoHoras: Long,
    val adminEmail: String,
    val adminSenha: String
)