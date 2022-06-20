package com.amade.api.repository

import com.amade.api.model.food.Produto
import com.amade.api.model.food.ProdutoImagem
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProdutoRepository : CoroutineCrudRepository<Produto, Int> {

    @Modifying
    @Query("INSERT INTO produto_imagem (produto_id,imagem) values(:?1,:?2)")
    suspend fun inserir_imagem(itemId: Int, image: String): Int

    @Query("select * from produto limit 20 offset :start")
    suspend fun findAll(start: Int): Flow<Produto>

    @Query("SELECT * FROM produto_imagem where produto_id=$1")
    fun obter_todas_imagens(id: Int): Flow<ProdutoImagem>

    @Modifying
    @Query("DELETE FROM produto where id=:id")
    suspend fun deleteProdutoById(id: Int): Int

    @Modifying
    @Query("INSERT INTO usuario_cart (produto_id,usuario_id) values (:?1,:?2)")
    suspend fun adicionar_no_carrinho_de_compras(itemId: Int, userId: String): Int

    @Modifying
    @Query("DELETE FROM usuario_cart where produto_id=:itemId and usuario_id=:userId")
    suspend fun remover_do_carrinho_de_compras(itemId: Int, userId: String): Int

    @Query("SELECT produto.* FROM produto inner join usuario_cart on produto.id = usuario_cart.produto_id and usuario_cart.usuario_id=:userId")
    fun obter_todos_items_do_usuario(userId: String): Flow<Produto>

    @Modifying
    @Query("INSERT INTO usuario_produto (produto_id,usuario_id) values (:?1,:?2)")
    suspend fun adicionar_na_lista_de_desejos(itemId: Int, userId: String): Int

    @Modifying
    @Query("DELETE FROM usuario_produto where produto_id=:itemId and usuario_id=:userId")
    suspend fun remover_da_lista_de_desejos(itemId: Int, userId: String): Int

    @Query("SELECT produto.* FROM produto inner join usuario_produto on produto.id = usuario_produto.produto_id and usuario_produto.usuario_id=:userId")
    fun obter_todos_items_da_lista_de_desejos_do_usuario(userId: String): Flow<Produto>

    @Query("select * from produto where upper(produto.nome) like upper(concat($1,'%')) order by nome")
    fun pesquisar(query: String): Flow<Produto>

    @Query("select exists(select * from usuario_produto where usuario_id=$1 and produto_id=$2)")
    suspend fun verificar_existencia_do_produto_na_lista_de_desejos(userId: String, produtoId: Int): Boolean

    @Query("select exists(select * from usuario_cart where usuario_id=$1 and produto_id=$2)")
    suspend fun verificar_existencia_do_produto_no_carrinho_de_compras(userId: String, itemId: Int): Boolean


//    @Modifying
//    @Query("DELETE from usuarioitemlike where userid=$1 and itemid=$2")
//    suspend fun remover_like_do_usuario(userId: String, itemId: Int): Int
//
//
//    @Modifying
//    @Query("update item set likes=likes+1 where id=$1")
//    suspend fun adicionar_like_ao_item(itemId: Int): Int
//
//    @Modifying
//    @Query("update item set likes=likes-1 where id=$1")
//    suspend fun remover_like_do_item(itemId: Int): Int
//
//    @Query("select exists(select * from usuarioitemlike where userid=$1 and itemid=$2)")
//    suspend fun verificar_existencia_de_like_do_usuario(userId: String, itemId: Int): Boolean
}