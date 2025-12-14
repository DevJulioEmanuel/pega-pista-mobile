package com.example.pegapista.data.repository

import com.example.pegapista.data.models.Corrida
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CorridaRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun salvarCorrida(corrida: Corrida): Result<Boolean> {
        return try {
            val user = auth.currentUser ?: throw Exception("Usuário não logado")

            val corridaSalva = corrida.copy(userId = user.uid)

            db.collection("corridas")
                .document(corrida.id)
                .set(corridaSalva)
                .await()

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun gerarIdCorrida(): String {
        return db.collection("corridas").document().id
    }
}