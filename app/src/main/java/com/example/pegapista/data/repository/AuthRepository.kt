package com.example.pegapista.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Função de Login
    suspend fun login(email: String, senha: String): Result<Boolean> {
        return try {
            auth.signInWithEmailAndPassword(email, senha).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Função de Cadastro (Cria Auth + Salva no Firestore)
    suspend fun cadastrar(nome: String, email: String, senha: String): Result<Boolean> {
        return try {
            // 1. Criar no Auth
            val authResult = auth.createUserWithEmailAndPassword(email, senha).await()
            val userId = authResult.user?.uid ?: throw Exception("Erro ao gerar ID")

            // 2. Salvar no Firestore
            val usuarioMap = hashMapOf(
                "nome" to nome,
                "email" to email,
                "uid" to userId
            )

            db.collection("usuarios").document(userId).set(usuarioMap).await()

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Pegar usuário atual (útil para verificar se já está logado)
    fun getCurrentUser() = auth.currentUser
}