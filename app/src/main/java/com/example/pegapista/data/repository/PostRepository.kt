package com.example.pegapista.data.repository

import android.content.Context
import androidx.work.*
import com.example.pegapista.data.models.Comentario
import com.example.pegapista.data.models.Postagem
import com.example.pegapista.database.AppDatabase
import com.example.pegapista.database.entities.PostagemEntity
import com.example.pegapista.utils.toModel
import com.example.pegapista.worker.SyncPostagemWorker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.UUID

class PostRepository(
    private val db: AppDatabase,
    private val context: Context
) {
    private val auth = FirebaseAuth.getInstance()
    private val postagemDao = db.postagemDao()
    private val workManager = WorkManager.getInstance(context)
    private val remoteDb = FirebaseFirestore.getInstance()


    suspend fun criarPost(postagem: Postagem): Result<Boolean> {
        return try {
            val user = auth.currentUser ?: throw Exception("Usuário não logado")

            val fotosConcatenadas = postagem.urlsFotos.joinToString(separator = ";")

            val entity = PostagemEntity(
                id = postagem.id.ifEmpty { UUID.randomUUID().toString() },
                userId = user.uid,
                autorNome = postagem.autorNome.ifBlank { user.displayName ?: "Corredor" },
                titulo = postagem.titulo,
                descricao = postagem.descricao,
                distanciaKm = postagem.corrida.distanciaKm,
                tempo = postagem.corrida.tempo,
                pace = postagem.corrida.pace,
                data = System.currentTimeMillis(),

                fotoUrl = fotosConcatenadas,

                postsincronizado = false
            )

            postagemDao.salvarPostagem(entity)
     //       android.util.Log.d("SYNC_DEBUG", "0.5. Salvo no Room com sucesso!")

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
            ExistingWorkPolicy.REPLACE,
            syncRequest
        )
    }

    fun gerarIdPost(): String = UUID.randomUUID().toString()

    suspend fun excluirPost(postId: String): Result<Unit> {
        return try {
            remoteDb.collection("posts").document(postId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFeedPosts(listaIds: List<String>): List<Postagem> {
        if (listaIds.isEmpty()) return emptyList()
        return try {
            val snapshot = remoteDb.collection("posts")
                .whereIn("userId", listaIds)
                .orderBy("data", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.toObjects(Postagem::class.java)
        } catch (e: Exception) {
            android.util.Log.e("FEED_ERRO", "Erro: ${e.message}")
            emptyList()
        }
    }

    fun gerarIdPost(): String {
        return db.collection("posts").document().id
    }

    suspend fun getPostsPorUsuario(userId: String): List<Postagem> {
        return try {
            val snapshot = db.collection("posts")
                .whereEqualTo("userId", userId)
                .orderBy("data", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            snapshot.toObjects(Postagem::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    // CURTIDA E COMENTARIOS - JULIO EMANUEL

    suspend fun toggleCurtida(postId: String, userId: String, jaCurtiu: Boolean): Boolean {
        return try {
            val postRef = remoteDb.collection("posts").document(postId)

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
            val batch = remoteDb.batch()
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
            val snapshot = remoteDb.collection("posts").document(postId)
                .collection("comentarios")
                .orderBy("data")
                .get()
                .await()
            snapshot.toObjects(Comentario::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    //UPLOAD IMAGEM

    suspend fun uploadImagem(uri: Uri): String? {
        return try {
            val ref = storage.reference.child("corridas/${System.currentTimeMillis()}.jpg")
            ref.putFile(uri).await()
            ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


}