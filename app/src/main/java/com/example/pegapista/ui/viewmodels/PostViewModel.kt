package com.example.pegapista.ui.viewmodels

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pegapista.data.models.Comentario
import com.example.pegapista.data.models.Corrida
import com.example.pegapista.data.models.Notificacao
import com.example.pegapista.data.models.Postagem
import com.example.pegapista.data.models.TipoNotificacao
import com.example.pegapista.data.repository.NotificationRepository
import com.example.pegapista.data.repository.PostRepository
import com.example.pegapista.data.repository.UserRepository
import com.example.pegapista.utils.copiarImagemParaCache
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import comprimirImagem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID


data class PostUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

class PostViewModel(
    application: Application,
    private val repository: PostRepository,
    private val userRepository: UserRepository = UserRepository(),
    private val notificationRepository: NotificationRepository = NotificationRepository()
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(PostUiState())
    val uiState = _uiState.asStateFlow()

    private val _feedState = MutableStateFlow<List<Postagem>>(emptyList())
    val feedState = _feedState.asStateFlow()

    private val _comentariosState = MutableStateFlow<List<Comentario>>(emptyList())
    val comentariosState = _comentariosState.asStateFlow()

    private val auth = FirebaseAuth.getInstance()
    val meuId: String
        get() = auth.currentUser?.uid ?: ""

    private val _fotosSelecionadasUris = MutableStateFlow<List<Uri>>(emptyList())
    val fotosSelecionadasUris: StateFlow<List<Uri>> = _fotosSelecionadasUris

    init {
        carregarFeed()
    }

    fun adicionarFoto(uri: Uri) {
        _fotosSelecionadasUris.value += uri
    }

    fun limparFotos() {
        _fotosSelecionadasUris.value = emptyList()
    }


    fun compartilharCorrida(
        titulo: String,
        descricao: String,
        distancia: Double,
        tempo: String,
        pace: String
    ) {
        _uiState.value = PostUiState(isLoading = true)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val usuarioAtual = userRepository.getUsuarioAtual()


                val listaFotosFinal = _fotosSelecionadasUris.value.mapNotNull { uri ->
                    copiarImagemParaCache(getApplication(), uri)
                }

                val corridaDados = Corrida(
                    distanciaKm = distancia,
                    tempo = tempo,
                    pace = pace
                )
                val tituloFinal = titulo.ifBlank { "Treino finalizado" }
                val descricaoFinal = descricao.ifBlank { "Atividade registrada." }

                val novaPostagem = Postagem(
                    id = repository.gerarIdPost(),
                    autorNome = usuarioAtual.nickname,
                    userId = usuarioAtual.id,
                    titulo = tituloFinal,
                    descricao = descricaoFinal,
                    corrida = corridaDados,
                    urlsFotos = listaFotosFinal,
                    data = System.currentTimeMillis()
                )

                val resultado = repository.criarPost(novaPostagem)

                resultado.onSuccess {
                    _uiState.value = PostUiState(isSuccess = true)
                    limparFotos()
                }.onFailure { e ->
                    _uiState.value = PostUiState(error = e.message ?: "Erro desconhecido")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = PostUiState(error = e.message ?: "Erro ao criar post")
            }
        }
    }



    fun carregarFeed() {
        viewModelScope.launch {
            val uid = auth.currentUser?.uid ?: return@launch
            val idsAmigos = userRepository.getIdsSeguindo().toMutableList()
            idsAmigos.add(uid)
            val listaAmigosComLimite = idsAmigos.take(10)

            if (listaAmigosComLimite.isNotEmpty()) {
                val posts = repository.getFeedPosts(listaAmigosComLimite)
                _feedState.value = posts
            } else {
                _feedState.value = emptyList()
            }
        }
    }

    fun excluirPost(postId: String) {
        viewModelScope.launch {
            _uiState.value = PostUiState(isLoading = true)
            val resultado = repository.excluirPost(postId)
            resultado.onSuccess {
                carregarFeed()
                _uiState.value = PostUiState(isSuccess = true)
            }.onFailure { e ->
                _uiState.value = PostUiState(error = "Erro ao excluir: ${e.message}")
            }
        }
    }

    fun toggleCurtidaPost(post: Postagem) {
        viewModelScope.launch {
            val uid = meuId.ifEmpty { return@launch }
            val jaCurtiu = post.curtidas.contains(uid)
            val userAtual = userRepository.getUsuarioAtual()

            val sucesso = repository.toggleCurtida(post.id, uid, jaCurtiu)

            if (sucesso) {
                val novaListaFeed = _feedState.value.map { p ->
                    if (p.id == post.id) {
                        val novasCurtidas = p.curtidas.toMutableList()
                        if (jaCurtiu) novasCurtidas.remove(uid) else novasCurtidas.add(uid)
                        p.copy(curtidas = novasCurtidas)
                    } else {
                        p
                    }
                }
                _feedState.value = novaListaFeed

                if (!jaCurtiu && post.userId != uid) {
                    val novaNotificacao = Notificacao(
                        destinatarioId = post.userId,
                        remetenteId = uid,
                        remetenteNome = userAtual.nickname,
                        tipo = TipoNotificacao.CURTIDA,
                        mensagem = "${userAtual.nickname} curtiu a sua corrida!",
                        data = System.currentTimeMillis()
                    )
                    launch { notificationRepository.criarNotificacao(novaNotificacao) }
                }
            }
        }
    }

    fun enviarComentario(postId: String, remetenteId: String, texto: String) {
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
                if (remetenteId != meuId) {
                    val novaNotificacao = Notificacao(
                        destinatarioId = remetenteId,
                        remetenteId = meuId,
                        remetenteNome = usuario.nickname,
                        tipo = TipoNotificacao.COMENTARIO,
                        mensagem = "${usuario.nickname} comentou na sua corrida!",
                        data = System.currentTimeMillis()
                    )
                    launch { notificationRepository.criarNotificacao(novaNotificacao) }
                }
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

    fun formatarDataHora(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale("pt", "BR"))
        return sdf.format(Date(timestamp))
    }

    suspend fun getFotoPerfil(userId: String): String? {
        return try {
            userRepository.getUsuarioPorId(userId)?.fotoPerfilUrl
        } catch (e: Exception) {
            null
        }
    }
}