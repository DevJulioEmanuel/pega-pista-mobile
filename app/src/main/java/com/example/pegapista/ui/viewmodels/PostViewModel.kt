package com.example.pegapista.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pegapista.data.models.Corrida
import com.example.pegapista.data.models.Postagem
import com.example.pegapista.data.repository.PostRepository
import com.example.pegapista.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
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
    private val userRepository = UserRepository()
    private val _uiState = MutableStateFlow(PostUiState())
    val uiState = _uiState.asStateFlow()

    private val auth = FirebaseAuth.getInstance()

    //FEED -Julio
    private val _feedState = MutableStateFlow<List<Postagem>>(emptyList())
    val feedState = _feedState.asStateFlow()

    init {
        carregarFeed()
    }

    fun carregarFeed() {
        viewModelScope.launch {
            val posts = repository.getFeedPosts()
            _feedState.value = posts
        }
    }

    fun compartilharCorrida(
        titulo: String,
        descricao: String,
        distancia: Double,
        tempo: String,
        pace: String
    ) {
        _uiState.value = PostUiState(isLoading = true)

        viewModelScope.launch {

            val usuarioAtual = userRepository.getUsuarioAtual()
            val nomeAutor = usuarioAtual.nickname

            viewModelScope.launch {
                val corridaDados = Corrida(
                    distanciaKm = distancia,
                    tempo = tempo,
                    pace = pace
                )

                val novoId = repository.gerarIdPost()
                val novaPostagem = Postagem(
                    id = novoId,
                    autorNome = nomeAutor,
                    titulo = titulo,
                    descricao = descricao,
                    corrida = corridaDados
                )

                val resultado = repository.criarPost(novaPostagem)

                resultado.onSuccess {
                    _uiState.value = PostUiState(isSuccess = true)
                    carregarFeed()
                }.onFailure { e ->
                    _uiState.value = PostUiState(error = e.message ?: "Erro ao publicar")
                }
            }
        }
    }
}