package com.example.pegapista.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pegapista.data.models.Corrida
import com.example.pegapista.data.models.Postagem
import com.example.pegapista.data.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Estado da UI
data class PostUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

class PostViewModel : ViewModel() {
    private val repository = PostRepository()

    private val _uiState = MutableStateFlow(PostUiState())
    val uiState = _uiState.asStateFlow()

    fun compartilharCorrida(
        titulo: String,
        descricao: String,
        distancia: Double,
        tempo: String,
        pace: String
    ) {
        _uiState.value = PostUiState(isLoading = true)

        viewModelScope.launch {
            val corridaDados = Corrida(
                distanciaKm = distancia,
                tempo = tempo,
                pace = pace
            )

            val novoId = repository.gerarIdPost()
            val novaPostagem = Postagem(
                id = novoId,
                titulo = titulo,
                descricao = descricao,
                corrida = corridaDados
            )

            // 3. Salvar
            val resultado = repository.criarPost(novaPostagem)

            resultado.onSuccess {
                _uiState.value = PostUiState(isSuccess = true)
            }.onFailure { e ->
                _uiState.value = PostUiState(error = e.message ?: "Erro ao publicar")
            }
        }
    }
}