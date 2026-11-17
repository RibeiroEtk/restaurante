package br.pucpr.restaurante.seguranca

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class JwtFilter(
    private val jwtService: JwtService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val header = request.getHeader("Authorization")

        if (header != null && header.startsWith("Bearer ")) {
            val token = header.substring(7)
            val claims = jwtService.lerToken(token)

            if (claims != null) {
                val usuarioId = claims.subject.toLong()
                val papel = claims.get("papel", String::class.java)

                // Papéis no Spring Security precisam do prefixo ROLE_
                val autoridades = listOf(SimpleGrantedAuthority("ROLE_$papel"))
                val autenticacao = UsernamePasswordAuthenticationToken(usuarioId, null, autoridades)

                // A partir daqui, o Spring considera o usuário logado nesta requisição
                SecurityContextHolder.getContext().authentication = autenticacao
            }
        }

        // Sempre continua a cadeia de filtros. Se não houver token válido,
        // as regras do SecurityConfig decidem se a rota pode ser acessada
        filterChain.doFilter(request, response)
    }
}