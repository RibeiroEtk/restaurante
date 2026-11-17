package br.pucpr.restaurante.seguranca

import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtService: JwtService
) {

    // Usado para criptografar as senhas antes de salvar no banco
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            // API REST com token não usa cookie de sessão, então o CSRF não se aplica
            .csrf { it.disable() }
            // Cada requisição traz seu próprio token: o servidor não guarda sessão
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                // Cadastro e login: livres
                it.requestMatchers(HttpMethod.POST, "/usuarios", "/usuarios/login").permitAll()

                // Consultas do cardápio: livres
                it.requestMatchers(HttpMethod.GET, "/categorias/**", "/produtos/**").permitAll()

                // Ações destrutivas no cardápio: só ADMIN
                it.requestMatchers(HttpMethod.POST, "/categorias/**", "/produtos/**").hasRole("ADMIN")
                it.requestMatchers(HttpMethod.PUT, "/categorias/**", "/produtos/**").hasRole("ADMIN")
                it.requestMatchers(HttpMethod.DELETE, "/categorias/**", "/produtos/**").hasRole("ADMIN")

                // Pedidos e dados do próprio usuário: precisa estar logado
                it.requestMatchers("/pedidos/**", "/usuarios/**").authenticated()

                // Swagger, página de erro etc.
                it.anyRequest().permitAll()
            }
            .exceptionHandling {
                // Sem token ou token inválido → 401
                it.authenticationEntryPoint { _, response, _ ->
                    escreverErro(response, 401, "Unauthorized", "Você precisa estar logado (envie o token JWT)")
                }
                // Logado, mas sem permissão → 403
                it.accessDeniedHandler { _, response, _ ->
                    escreverErro(response, 403, "Forbidden", "Você não tem permissão para esta ação")
                }
            }
            // Nosso filtro roda antes do filtro padrão de login do Spring
            .addFilterBefore(JwtFilter(jwtService), UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    // Monta a resposta de erro no mesmo formato ProblemDetail do resto da API
    private fun escreverErro(response: HttpServletResponse, status: Int, titulo: String, mensagem: String) {
        response.status = status
        response.contentType = "application/problem+json"
        response.characterEncoding = "UTF-8"
        response.writer.write(
            """{"title":"$titulo","status":$status,"detail":"$mensagem","errors":{"geral":["$mensagem"]}}"""
        )
    }
}