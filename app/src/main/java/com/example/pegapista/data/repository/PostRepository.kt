package com.example.pegapista.data.repository

import com.example.pegapista.data.models.Postagem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PostRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()


    suspend fun criarPost(post: Postagem): Result<Boolean> {
        return try {
            val user = auth.currentUser ?: throw Exception("Usuário não logado")

            val postSalvo = post.copy(userId = user.uid)

            db.collection("posts")
                .document(post.id)
                .set(postSalvo)
                .await()

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun gerarIdPost(): String {
        return db.collection("posts").document().id
    }
}