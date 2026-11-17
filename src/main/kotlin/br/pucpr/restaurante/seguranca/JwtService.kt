package br.pucpr.restaurante.seguranca

import br.pucpr.restaurante.usuarios.Usuario
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.Date

@Component
class JwtService(
    private val properties: SegurancaProperties
) {
    private val log = LoggerFactory.getLogger(JwtService::class.java)

    // Chave secreta usada para assinar o token. O jjwt escolhe o algoritmo pelo tamanho
    // da chave: a nossa tem mais de 64 bytes, então ele usa HMAC-SHA512 (HS512)
    private val chave = Keys.hmacShaKeyFor(properties.jwtSecret.toByteArray())

    fun gerarToken(usuario: Usuario): String {
        val agora = Date()
        val milissegundos = properties.jwtExpiracaoHoras * 60 * 60 * 1000
        val expiracao = Date(agora.time + milissegundos)

        return Jwts.builder()
            .issuer("Restaurante API")
            .subject(usuario.id.toString())
            .claim("email", usuario.email)
            .claim("papel", usuario.papel)
            .issuedAt(agora)
            .expiration(expiracao)
            .signWith(chave)
            .compact()
    }

    // Devolve os dados (claims) do token, ou null se o token for inválido ou estiver expirado
    fun lerToken(token: String): Claims? {
        try {
            return Jwts.parser()
                .verifyWith(chave)
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (e: JwtException) {
            log.warn("Token JWT inválido: {}", e.message)
            return null
        } catch (e: IllegalArgumentException) {
            log.warn("Token JWT vazio ou mal formatado")
            return null
        }
    }
}