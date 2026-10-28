package br.pucpr.restaurante.erros

import org.slf4j.LoggerFactory
import org.springframework.beans.TypeMismatchException
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    // 1) Erros de validação do @Valid (@NotBlank, @Size...)
    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val errors = mutableMapOf<String, MutableList<String>>()

        for (fieldError in ex.bindingResult.fieldErrors) {
            val campo = fieldError.field
            val mensagem = fieldError.defaultMessage ?: "Valor inválido"

            if (!errors.containsKey(campo)) {
                errors[campo] = mutableListOf()
            }
            errors[campo]!!.add(mensagem)
        }

        log.warn("Erro de validação: {}", errors)

        val problem = ProblemDetail.forStatusAndDetail(status, "Um ou mais campos são inválidos")
        problem.setProperty("errors", errors)
        return handleExceptionInternal(ex, problem, headers, status, request)
    }

    // 2) JSON mal formatado (erro do Jackson)
    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        log.warn("JSON inválido recebido: {}", ex.message)

        val problem = ProblemDetail.forStatusAndDetail(status, "Invalid JSON request")
        problem.setProperty("errors", mapOf("body" to listOf("Malformed JSON request")))
        return handleExceptionInternal(ex, problem, headers, status, request)
    }

    // 3) Parâmetro com tipo errado (ex: /categorias/abc em vez de número)
    override fun handleTypeMismatch(
        ex: TypeMismatchException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        val parametro = ex.propertyName ?: "parametro"
        log.warn("Parâmetro com tipo inválido: {}={}", parametro, ex.value)

        val problem = ProblemDetail.forStatusAndDetail(status, "Parâmetro com valor inválido")
        problem.setProperty("errors", mapOf(parametro to listOf("Valor '${ex.value}' é inválido")))
        return handleExceptionInternal(ex, problem, headers, status, request)
    }

    // 4)  exceções (@ResponseStatus) e qualquer erro inesperado
    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ProblemDetail {
        val responseStatus = AnnotationUtils.findAnnotation(ex.javaClass, ResponseStatus::class.java)

        // Exceção, anotada com @ResponseStatus (NotFound, BadRequest)
        if (responseStatus != null) {
            val status = responseStatus.value
            val mensagem = ex.message ?: status.reasonPhrase

            val problem = ProblemDetail.forStatusAndDetail(status, mensagem)
            problem.setProperty("errors", mapOf("geral" to listOf(mensagem)))
            return problem
        }

        // Erro inesperado: vira 500 sem expor detalhes
        log.error("Erro inesperado no servidor", ex)

        val problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Ocorreu um erro interno no servidor"
        )
        problem.setProperty("errors", mapOf("geral" to listOf("Erro interno. Tente novamente mais tarde.")))
        return problem
    }
}