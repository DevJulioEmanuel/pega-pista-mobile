package com.example.pegapista.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pegapista.data.models.Comentario
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


    // ESTADO - FEED
    private val _feedState = MutableStateFlow<List<Postagem>>(emptyList())
    val feedState = _feedState.asStateFlow()

    // ESTADO - COMENTARIOS
    private val _comentariosState = MutableStateFlow<List<Comentario>>(emptyList())
    val comentariosState = _comentariosState.asStateFlow()

    // ESTADO - ID
    private val auth = FirebaseAuth.getInstance()
    val meuId: String
        get() = auth.currentUser?.uid ?: ""

    init {
        carregarFeed()
    }

    fun carregarFeed() {
        viewModelScope.launch {
            val meuId = auth.currentUser?.uid

            if (meuId == null) {
                _feedState.value = emptyList()
                return@launch
            }
            val idsAmigos = userRepository.getIdsSeguindo()
            val listaAmigos = idsAmigos.toMutableList()
            listaAmigos.add(meuId)

            val listaAmigosComLimite = listaAmigos.take(10)

            if (listaAmigosComLimite.isNotEmpty()) {
                val posts = repository.getFeedPosts(listaAmigosComLimite)
                _feedState.value = posts
            } else {
                _feedState.value = emptyList()
            }
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

    // CURTIDAS E COMENTARIOS - JULIO EMANUEL

    fun toggleCurtidaPost(post: Postagem) {
        viewModelScope.launch {
            val meuId = auth.currentUser?.uid ?: return@launch
            val jaCurtiu = post.curtidas.contains(meuId)

            val sucesso = repository.toggleCurtida(post.id, meuId, jaCurtiu)

            if (sucesso) {
                val novaListaFeed = _feedState.value.map { p ->
                    if (p.id == post.id) {
                        val novasCurtidas = p.curtidas.toMutableList()
                        if (jaCurtiu) novasCurtidas.remove(meuId) else novasCurtidas.add(meuId)
                        p.copy(curtidas = novasCurtidas)
                    } else {
                        p
                    }
                }
                _feedState.value = novaListaFeed
            }
        }
    }

    fun enviarComentario(postId: String, texto: String) {
        if (texto.isBlank()) return

        viewModelScope.launch {
            val usuario = userRepository.getUsuarioAtual()
            val novoComentario = Comentario(
                userId = usuario.id,
                nomeUsuario = usuario.nickname,
                texto = texto,
                data = System.currentTimeMillis()
            )
            val sucesso = repository.enviarComentario(postId, novoComentario)
            if (sucesso) {
                carregarComentarios(postId)
            }
        }
    }

    fun carregarComentarios(postId: String) {
        viewModelScope.launch {
            val lista = repository.getComentarios(postId)
            _comentariosState.value = lista
        }
    }
}