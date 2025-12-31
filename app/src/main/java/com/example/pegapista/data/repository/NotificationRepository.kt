package com.example.pegapista.data.repository

import com.example.pegapista.data.models.Notificacao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class NotificationRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    suspend fun criarNotificacao(notificacao: Notificacao) {
        db.collection("notificacoes").add(notificacao).await()
    }

    suspend fun excluirNotificacao(notificacaoId: String) {
        try {
            db.collection("notificacoes").document(notificacaoId).delete().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun limparTodasNotificacoes(userId: String) {
        try {
            val snapshot = db.collection("notificacoes")
                .whereEqualTo("destinatarioId", userId)
                .get()
                .await()

            val batch = db.batch()
            for (document in snapshot.documents) {
                batch.delete(document.reference)
            }
            batch.commit().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}