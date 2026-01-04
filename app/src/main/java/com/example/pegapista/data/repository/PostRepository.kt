package com.example.pegapista.data.repository

import android.net.Uri
import android.util.Log
import com.example.pegapista.data.models.Comentario
import com.example.pegapista.data.models.Postagem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class PostRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()


    suspend fun criarPost(post: Postagem): Result<Boolean> {
        return try {
            val user = auth.currentUser ?: throw Exception("Usuário não logado")
            val postSalvo = post.copy(userId = user.uid)

            db.collection("posts")
                .document(post.id)
                .set(postSalvo)
                .await()
            UserRepository().atualizarSequenciaDiaria()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun excluirPost(postId: String): Result<Unit> {
        return try {
            db.collection("posts").document(postId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFeedPosts(listaIds: List<String>): List<Postagem> {
        if (listaIds.isEmpty()) return emptyList()
        return try {
            val snapshot = db.collection("posts")
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
            val postRef = db.collection("posts").document(postId)

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
            val batch = db.batch()

            val novoComentarioRef = db.collection("posts").document(postId)
                .collection("comentarios").document()

            batch.set(novoComentarioRef, comentario)

            val postRef = db.collection("posts").document(postId)
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
            val snapshot = db.collection("posts").document(postId)
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