package com.example.pegapista.data.repository

import android.util.Log
import com.example.pegapista.data.models.Postagem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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

    suspend fun getFeedPosts(): List<Postagem> {
        return try {
            val snapshot = db.collection("posts")
                .orderBy("data", Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .await()
            snapshot.toObjects(Postagem::class.java)

        } catch (e: Exception) {
            emptyList()
        }
    }

    fun gerarIdPost(): String {
        return db.collection("posts").document().id
    }
}