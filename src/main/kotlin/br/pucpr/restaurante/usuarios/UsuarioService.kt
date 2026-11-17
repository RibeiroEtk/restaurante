package br.pucpr.restaurante.usuarios

import br.pucpr.restaurante.erros.BadRequestException
import br.pucpr.restaurante.erros.NotFoundException
import br.pucpr.restaurante.erros.UnauthorizedException
import br.pucpr.restaurante.seguranca.JwtService
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UsuarioService(
    private val repository: UsuarioRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService
) {
    private val log = LoggerFactory.getLogger(UsuarioService::class.java)

    fun cadastrar(request: UsuarioRequest): Usuario {
        val email = request.email!!.trim().lowercase()

        if (repository.findByEmailIgnoreCase(email) != null) {
            log.warn("Tentativa de cadastro com e-mail já existente. email={}", email)
            throw BadRequestException("Já existe um usuário com o e-mail '$email'")
        }

        val usuario = Usuario(
            nome = request.nome!!.trim(),
            email = email,
            senha = passwordEncoder.encode(request.senha!!)!!,  // nunca salvamos a senha pura
            papel = "USER"
        )
        val salvo = repository.save(usuario)
        log.info("Usuário cadastrado. id={}, email={}", salvo.id, salvo.email)
        return salvo
    }

    fun login(request: LoginRequest): LoginResponse {
        val email = request.email!!.trim()
        val usuario = repository.findByEmailIgnoreCase(email)

        // Mesma mensagem para e-mail inexistente e senha errada:
        // assim um atacante não descobre quais e-mails estão cadastrados
        if (usuario == null || !passwordEncoder.matches(request.senha!!, usuario.senha)) {
            log.warn("Falha de login. email={}", email)
            throw UnauthorizedException("E-mail ou senha inválidos")
        }

        val token = jwtService.gerarToken(usuario)
        log.info("Login realizado. id={}, papel={}", usuario.id, usuario.papel)
        return LoginResponse(token, UsuarioResponse(usuario))
    }

    fun buscarPorId(id: Long): Usuario {
        val usuario = repository.findById(id).orElse(null)
        if (usuario == null) {
            log.warn("Usuário não encontrado. id={}", id)
            throw NotFoundException("Usuário com id $id não encontrado")
        }
        return usuario
    }

    // Chamado ao iniciar o servidor: garante que exista um administrador
    fun criarAdminSeNaoExistir(email: String, senha: String) {
        if (repository.findByEmailIgnoreCase(email) != null) {
            return
        }
        val admin = Usuario(
            nome = "Administrador",
            email = email,
            senha = passwordEncoder.encode(senha)!!,
            papel = "ADMIN"
        )
        repository.save(admin)
        log.info("Usuário administrador criado. email={}", email)
    }
}