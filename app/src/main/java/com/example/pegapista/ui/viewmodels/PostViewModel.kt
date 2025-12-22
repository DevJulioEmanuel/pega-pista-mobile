package com.example.pegapista.ui.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pegapista.data.models.Corrida
import com.example.pegapista.data.models.Postagem
import com.example.pegapista.data.repository.PostRepository
import com.example.pegapista.data.repository.UserRepository
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

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

    private val _fotoSelecionadaUri = MutableStateFlow<Uri?>(null)
    val fotoSelecionadaUri = _fotoSelecionadaUri.asStateFlow()

    // Estado do Feed
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

    fun selecionarFotoLocal(uri: Uri) {
        _fotoSelecionadaUri.value = uri
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
            try {
                val usuarioAtual = userRepository.getUsuarioAtual()
                val nomeAutor = usuarioAtual.nickname
                val fotoUri = _fotoSelecionadaUri.value

                var urlFotoFinal: String? = null

                if (fotoUri != null) {
                    try {
                        val storageRef = FirebaseStorage.getInstance().reference
                        val nomeArquivo = "corridas/${usuarioAtual.id}/${UUID.randomUUID()}.jpg"
                        val fotoRef = storageRef.child(nomeArquivo)

                        fotoRef.putFile(fotoUri).await()
                        urlFotoFinal = fotoRef.downloadUrl.await().toString()
                    } catch (e: Exception) {
                        Log.e("POST_VM", "Erro upload foto: ${e.message}")
                    }
                }

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
                    corrida = corridaDados,
                    fotoUrl = urlFotoFinal
                )

                val resultado = repository.criarPost(novaPostagem)

                resultado.onSuccess {
                    _uiState.value = PostUiState(isSuccess = true)
                    carregarFeed()
                }.onFailure { e ->
                    _uiState.value = PostUiState(error = e.message ?: "Erro ao publicar")
                }

            } catch (e: Exception) {
                _uiState.value = PostUiState(error = "Erro geral: ${e.message}")
            }
        }
    }
}