package com.example.pegapista.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pegapista.data.models.Notificacao
import com.example.pegapista.data.repository.NotificationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class NotificationsViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val repository = NotificationRepository()

    private val _notificacoes = MutableStateFlow<List<Notificacao>>(emptyList())
    val notificacoes: StateFlow<List<Notificacao>> = _notificacoes

    fun carregarNotificacoes() {
        val meuId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            try {
                val snapshot = db.collection("notificacoes")
                    .whereEqualTo("destinatarioId", meuId)
                    .orderBy("data", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .get()
                    .await()

                val lista = snapshot.documents.mapNotNull { doc ->
                    val notificacao = doc.toObject(Notificacao::class.java)
                    notificacao?.copy(id = doc.id)
                }

                _notificacoes.value = lista

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun excluirNotificacao(id: String) {
        viewModelScope.launch {
            val listaAtual = _notificacoes.value.toMutableList()
            listaAtual.removeAll { it.id == id }
            _notificacoes.value = listaAtual

            repository.excluirNotificacao(id)
        }
    }

    fun limparTudo() {
        val meuId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _notificacoes.value = emptyList()
            repository.limparTodasNotificacoes(meuId)
        }
    }
}