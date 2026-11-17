# 🍔 Restaurante API

Trabalho final da disciplina **Backend: Web Services** — Bacharelado em Sistemas de Informação, PUCPR.

**Opção escolhida:** Opção 1 — Estendendo os serviços (servidor criado do zero).

## 👤 Integrante

- Gabriel Ribeiro Estefanski

## 🎥 Vídeo de apresentação

👉 **[Assistir no YouTube](COLE_AQUI_O_LINK_DO_VIDEO)**

## 📌 Sobre o projeto

API REST de um restaurante, desenvolvida em **Kotlin** com **Spring Boot**. Permite gerenciar o cardápio (categorias e produtos), cadastrar usuários com login via **JWT** e montar pedidos adicionando e removendo produtos.

## 🛠️ Tecnologias

- Kotlin + Spring Boot 4
- Spring Data JPA (Hibernate)
- Spring Security + JWT (jjwt 0.12.5)
- Banco H2 (em arquivo)
- Jakarta Validation
- SpringDoc OpenAPI (Swagger)
- Gradle (Kotlin DSL)

## 🗂️ Modelo de dados

| Entidade | Descrição |
|---|---|
| Categoria | Agrupa os produtos (ex: Bebidas, Lanches) |
| Produto | Item do cardápio, pertence a uma categoria |
| Usuario | Cliente (USER) ou administrador (ADMIN) |
| Pedido | Pedido feito por um usuário |
| ItemPedido | Tabela intermediária entre Pedido e Produto, com quantidade e preço |

**Relacionamentos:**

- **One-to-many:** Categoria → Produto e Usuario → Pedido
- **Many-to-many com tabela no meio:** Pedido ↔ Produto, através de ItemPedido (guarda a quantidade e o preço no momento da compra)

## 🔐 Segurança

- Login com **JWT**: o usuário faz login e recebe um token, que deve ser enviado no cabeçalho `Authorization: Bearer <token>`
- Senhas salvas com **BCrypt** (nunca em texto puro)
- Papéis: **USER** (cliente) e **ADMIN**
- Ações destrutivas no cardápio (criar, alterar e excluir) são exclusivas do **ADMIN**
- Pedidos exigem login, e cada cliente só acessa os próprios pedidos (o ADMIN acessa todos)
- Respostas: **401** sem login ou com token inválido; **403** sem permissão

**Administrador criado automaticamente ao iniciar:**
- E-mail: `admin@restaurante.com`
- Senha: `admin123`

(Em produção, a chave do JWT e os dados do admin devem vir das variáveis de ambiente `JWT_SECRET`, `ADMIN_EMAIL` e `ADMIN_SENHA`.)

## 🔗 Endpoints

### Usuários

| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| POST | `/api/usuarios` | Livre | Cadastro (sempre como USER) |
| POST | `/api/usuarios/login` | Livre | Login, devolve o token JWT |
| GET | `/api/usuarios/me` | Logado | Dados do usuário logado |

### Categorias

| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| GET | `/api/categorias` | Livre | Lista em ordem alfabética |
| GET | `/api/categorias/{id}` | Livre | Busca uma categoria |
| POST | `/api/categorias` | ADMIN | Cria |
| PUT | `/api/categorias/{id}` | ADMIN | Atualiza |
| DELETE | `/api/categorias/{id}` | ADMIN | Exclui (bloqueado se tiver produtos) |

### Produtos

| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| GET | `/api/produtos` | Livre | Lista com filtros e ordenação |
| GET | `/api/produtos/{id}` | Livre | Busca um produto |
| POST | `/api/produtos` | ADMIN | Cria |
| PUT | `/api/produtos/{id}` | ADMIN | Atualiza |
| DELETE | `/api/produtos/{id}` | ADMIN | Exclui (bloqueado se estiver em pedidos) |

**Filtros e ordenação** (`GET /api/produtos`), todos opcionais:

| Parâmetro | Descrição | Exemplo |
|---|---|---|
| `categoria` | Nome da categoria (não diferencia maiúsculas) | `bebidas` |
| `precoMax` | Preço máximo | `20` |
| `nome` | Parte do nome do produto | `burger` |
| `sortBy` | Campo de ordenação: `nome` ou `preco` (padrão: `nome`) | `preco` |
| `sortDir` | Direção: `ASC` ou `DESC` (padrão: `ASC`) | `DESC` |

Exemplo: `GET /api/produtos?categoria=lanches&sortBy=preco&sortDir=DESC`

### Pedidos (exigem login)

| Método | Rota | Descrição |
|---|---|---|
| POST | `/api/pedidos` | Cria um pedido vazio para o usuário logado |
| GET | `/api/pedidos` | Lista os pedidos (o cliente vê os seus; o ADMIN vê todos) |
| GET | `/api/pedidos/{id}` | Busca um pedido |
| POST | `/api/pedidos/{id}/itens` | **Adiciona um produto ao pedido** |
| DELETE | `/api/pedidos/{id}/itens/{produtoId}` | **Remove um produto do pedido** |
| PUT | `/api/pedidos/{id}/fechar` | Fecha o pedido (não pode mais ser alterado) |
| DELETE | `/api/pedidos/{id}` | Exclui o pedido |

Exemplo de body para adicionar item:

```json
{ "produtoId": 1, "quantidade": 2 }
```

## ⚠️ Tratamento de erros

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

## 📝 Logs

Os services registram as operações importantes (criação, alteração, exclusão, login e tentativas inválidas) com os níveis `INFO` e `WARN`. Os logs aparecem no console e também são gravados em `logs/spring.log`. Senhas nunca são registradas.

## ▶️ Como executar

1. Clone o repositório
2. Abra no IntelliJ IDEA (JDK 17)
3. Execute `RestauranteApplication.kt`, ou rode no terminal: `./gradlew bootRun`
4. Acesse o Swagger em: http://localhost:8080/api
5. Faça login em `POST /usuarios/login` com o admin, copie o token e clique em **Authorize** para usá-lo

## ✅ Status

- [x] Entidades e relacionamentos (one-to-many e many-to-many com tabela no meio)
- [x] CRUD completo de Categoria e Produto
- [x] Filtro e ordenação de produtos por parâmetros de query
- [x] Endpoints de associação entre Pedido e Produto
- [x] Autenticação JWT nas ações destrutivas
- [x] Validação, tratamento de erros e logs