package com.example.pegapista.data.repository

import com.example.pegapista.data.models.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


class AuthRepository {

    private val auth = FirebaseAuth.getInstance()

    fun signOut() {
        auth.signOut()
    }
    private val db = FirebaseFirestore.getInstance()

    suspend fun login(email: String, senha: String): Result<Boolean> {
        return try {
            auth.signInWithEmailAndPassword(email, senha).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cadastrar(nome: String, email: String, senha: String): Result<Boolean> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, senha).await()
            val user = authResult.user ?: throw Exception("Erro ao criar usuário")

            val novoUsuario = Usuario(
                id = user.uid,
                nickname = nome,
                email = email,
            )

            db.collection("usuarios").document(user.uid).set(novoUsuario).await()

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCurrentUser() = auth.currentUser
}