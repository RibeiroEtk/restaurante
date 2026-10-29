#  Restaurante API

Trabalho da disciplina **Backend: Web Services** — Bacharelado em Sistemas de Informação, PUCPR.

**Opção escolhida:** Opção 1 — Estendendo os serviços (servidor criado do zero).

##  Integrante

- Gabriel Ribeiro Estefanski

##  Vídeo de apresentação

*(será adicionado na entrega final)*

##  Sobre o projeto

API REST de um restaurante, desenvolvida em **Kotlin** com **Spring Boot**, que permite gerenciar categorias, produtos e pedidos.

##  Tecnologias

- Kotlin + Spring Boot 4
- Spring Data JPA (Hibernate)
- Banco H2 (em arquivo)
- Jakarta Validation
- SpringDoc OpenAPI (Swagger)
- Gradle (Kotlin DSL)

##  Modelo de dados

| Entidade | Descrição |
|---|---|
| Categoria | Agrupa os produtos (ex: Bebidas, Lanches) |
| Produto | Item do cardápio, pertence a uma categoria |
| Usuario | Cliente ou administrador do sistema |
| Pedido | Pedido feito por um usuário |
| ItemPedido | Tabela intermediária entre Pedido e Produto, com quantidade e preço |

**Relacionamentos:**

- **One-to-many:** Categoria → Produto e Usuario → Pedido
- **Many-to-many com tabela no meio:** Pedido ↔ Produto, através de ItemPedido (guarda a quantidade e o preço no momento da compra)

##  Endpoints

### Categorias

| Método | Rota | Descrição |
|---|---|---|
| GET | `/api/categorias` | Lista as categorias em ordem alfabética |
| GET | `/api/categorias/{id}` | Busca uma categoria |
| POST | `/api/categorias` | Cria uma categoria |
| PUT | `/api/categorias/{id}` | Atualiza uma categoria |
| DELETE | `/api/categorias/{id}` | Exclui uma categoria (bloqueado se tiver produtos) |

### Produtos

| Método | Rota | Descrição |
|---|---|---|
| GET | `/api/produtos` | Lista os produtos com filtros e ordenação |
| GET | `/api/produtos/{id}` | Busca um produto |
| POST | `/api/produtos` | Cria um produto |
| PUT | `/api/produtos/{id}` | Atualiza um produto |
| DELETE | `/api/produtos/{id}` | Exclui um produto |

**Filtros e ordenação** (`GET /api/produtos`), todos opcionais:

| Parâmetro | Descrição | Exemplo |
|---|---|---|
| `categoria` | Nome da categoria (não diferencia maiúsculas) | `bebidas` |
| `precoMax` | Preço máximo | `20` |
| `nome` | Parte do nome do produto | `burger` |
| `sortBy` | Campo de ordenação: `nome` ou `preco` (padrão: `nome`) | `preco` |
| `sortDir` | Direção: `ASC` ou `DESC` (padrão: `ASC`) | `DESC` |

Exemplo: `GET /api/produtos?categoria=lanches&sortBy=preco&sortDir=DESC`

##  Tratamento de erros

Todos os erros seguem o padrão **ProblemDetail (RFC 9457)**, com o campo extra `errors`:

```json
{
  "detail": "Um ou mais campos são inválidos",
  "instance": "/api/categorias",
  "status": 400,
  "title": "Bad Request",
  "errors": {
    "nome": ["O nome é obrigatório"]
  }
}
```

##  Como executar

1. Clone o repositório
2. Abra no IntelliJ IDEA (JDK 17)
3. Execute `RestauranteApplication.kt`, ou rode no terminal: `./gradlew bootRun`
4. Acesse a documentação Swagger em: http://localhost:8080/api

##  Status

- [x] Entidades e relacionamentos
- [x] CRUD de Categoria
- [x] CRUD de Produto com filtro e ordenação
- [x] Validação, tratamento de erros e logs
- [ ] Endpoints de Pedido (adicionar e remover produtos)
- [ ] Autenticação JWT nas ações destrutivas