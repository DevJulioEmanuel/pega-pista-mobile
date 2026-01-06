package com.example.pegapista.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.pegapista.database.entities.PostagemEntity

@Dao
interface PostagemDao {
@Insert(onConflict = OnConflictStrategy.REPLACE)
suspend fun salvarPostagem(postagem: PostagemEntity)

@Query("SELECT * FROM postagem WHERE postsincronizado = 0")
suspend fun getPostagemNaoSincronizada(): List<PostagemEntity>

@Update
suspend fun atualizarPostagem(postagem: PostagemEntity)

@Query("SELECT * FROM postagem ORDER BY data DESC")
suspend fun getTodasPostagem(): List<PostagemEntity>
}
