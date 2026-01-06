package com.example.pegapista.data.repository

import android.content.Context
import androidx.work.*
import com.example.pegapista.data.models.Comentario
import com.example.pegapista.data.models.Postagem
import com.example.pegapista.database.AppDatabase
import com.example.pegapista.database.entities.PostagemEntity
import com.example.pegapista.worker.SyncPostagemWorker // <--- Importante: Certifica que o pacote está certo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.UUID

class PostRepository(
    private val db: AppDatabase, // Este é o banco LOCAL (Room)
    private val context: Context
) {
    private val auth = FirebaseAuth.getInstance()
    private val postagemDao = db.postagemDao()
    private val workManager = WorkManager.getInstance(context)

    // CRIEI ESTA VARIÁVEL PARA O FIREBASE
    private val remoteDb = FirebaseFirestore.getInstance()

    // ----------------------------------------------------------------
    // PARTE 1: OFFLINE FIRST (Salvar Post)
    // Usa o Banco Local (Room) e o WorkManager
    // ----------------------------------------------------------------

    suspend fun criarPost(postagem: Postagem): Result<Boolean> {
        return try {
            val user = auth.currentUser ?: throw Exception("Usuário não logado")

            // Converte o Model para Entity do Room
            val entity = PostagemEntity(
                id = postagem.id.ifEmpty { UUID.randomUUID().toString() },
                userId = user.uid,
                autorNome = user.displayName ?: "Corredor",
                titulo = postagem.titulo,
                descricao = postagem.descricao,
                corrida = postagem.corrida,
                data = System.currentTimeMillis(),
                fotoUrl = postagem.urlsFotos.firstOrNull(),
                postsincronizado = false
            )

            // Salva no Room (db local)
            postagemDao.salvarPostagem(entity)

            // Agenda o envio (WorkManager)
            agendarSincronizacao()

            Result.success(true)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    private fun agendarSincronizacao() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<SyncPostagemWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniqueWork(
            "sync_postagens",
            ExistingWorkPolicy.APPEND,
            syncRequest
        )
    }

    fun gerarIdPost(): String {
        return UUID.randomUUID().toString()
    }
    // No PostRepository.kt, adiciona isto:

    suspend fun getPostsPorUsuario(userId: String): List<Postagem> {
        return try {
            val snapshot = remoteDb.collection("posts")
                .whereEqualTo("userId", userId)
                .orderBy("data", Query.Direction.DESCENDING)
                .get()
                .await()
            snapshot.toObjects(Postagem::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Função auxiliar para deletar (agora deleta do Firebase direto, pode melhorar depois)
    suspend fun excluirPost(postId: String): Result<Unit> {
        return try {
            remoteDb.collection("posts").document(postId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ----------------------------------------------------------------
    // PARTE 2: ONLINE (Leitura de Feed, Comentários, Curtidas)
    // Usa o Firebase (remoteDb) porque ainda não implementamos cache disso
    // ----------------------------------------------------------------

    suspend fun getFeedPosts(listaIds: List<String>): List<Postagem> {
        if (listaIds.isEmpty()) return emptyList()
        return try {
            val snapshot = remoteDb.collection("posts") // Usa remoteDb aqui!
                .whereIn("userId", listaIds)
                .orderBy("data", Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.toObjects(Postagem::class.java)

        } catch (e: Exception) {
            android.util.Log.e("FEED_ERRO", "Erro: ${e.message}")
            emptyList()
        }
    }

    suspend fun toggleCurtida(postId: String, userId: String, jaCurtiu: Boolean): Boolean {
        return try {
            val postRef = remoteDb.collection("posts").document(postId) // Usa remoteDb aqui!

            if (jaCurtiu) {
                postRef.update("curtidas", FieldValue.arrayRemove(userId)).await()
            } else {
                postRef.update("curtidas", FieldValue.arrayUnion(userId)).await()
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun enviarComentario(postId: String, comentario: Comentario): Boolean {
        return try {
            val batch = remoteDb.batch() // Usa remoteDb aqui!

            val novoComentarioRef = remoteDb.collection("posts").document(postId)
                .collection("comentarios").document()

            batch.set(novoComentarioRef, comentario)

            val postRef = remoteDb.collection("posts").document(postId)
            batch.update(postRef, "qtdComentarios", FieldValue.increment(1))

            batch.commit().await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getComentarios(postId: String): List<Comentario> {
        return try {
            val snapshot = remoteDb.collection("posts").document(postId) // Usa remoteDb aqui!
                .collection("comentarios")
                .orderBy("data")
                .get()
                .await()
            snapshot.toObjects(Comentario::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}