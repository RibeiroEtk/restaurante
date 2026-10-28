package br.pucpr.restaurante.produtos

import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ProdutoRepository : JpaRepository<Produto, Long> {

    @Query(
        """
        SELECT p FROM Produto p
        JOIN FETCH p.categoria c
        WHERE (:categoria IS NULL OR UPPER(c.nome) = UPPER(:categoria))
          AND (:precoMax IS NULL OR p.preco <= :precoMax)
          AND (:nome IS NULL OR UPPER(p.nome) LIKE UPPER(CONCAT('%', :nome, '%')))
        """
    )
    fun buscarComFiltros(
        @Param("categoria") categoria: String?,
        @Param("precoMax") precoMax: Double?,
        @Param("nome") nome: String?,
        sort: Sort
    ): List<Produto>
}