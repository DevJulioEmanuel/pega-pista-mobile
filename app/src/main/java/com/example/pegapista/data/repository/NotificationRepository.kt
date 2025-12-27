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

}