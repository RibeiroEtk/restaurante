package br.pucpr.restaurante.usuarios

import br.pucpr.restaurante.seguranca.SegurancaProperties
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

// O ApplicationRunner é executado uma vez, logo depois que o servidor sobe
@Component
class CriarAdminAoIniciar(
    private val usuarioService: UsuarioService,
    private val properties: SegurancaProperties
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        usuarioService.criarAdminSeNaoExistir(properties.adminEmail, properties.adminSenha)
    }
}