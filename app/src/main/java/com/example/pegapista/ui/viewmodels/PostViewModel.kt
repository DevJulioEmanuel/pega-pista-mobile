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
import com.google.firebase.Firebase
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

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PostRepository()
    private val userRepository = UserRepository()
    private val notificationRepository = NotificationRepository()
    private val _uiState = MutableStateFlow(PostUiState())
    val uiState = _uiState.asStateFlow()

    private val storage = Firebase.storage

    // ESTADO - FEED
    private val _feedState = MutableStateFlow<List<Postagem>>(emptyList())
    val feedState = _feedState.asStateFlow()

    // ESTADO - COMENTARIOS
    private val _comentariosState = MutableStateFlow<List<Comentario>>(emptyList())
    val comentariosState = _comentariosState.asStateFlow()

    // ESTADO - ID
    private val auth = FirebaseAuth.getInstance()

    // IMAGENS (agora no plural - JULIO EMANUEL)
    private val _fotosSelecionadasUris = MutableStateFlow<List<Uri>>(emptyList())
    val fotosSelecionadasUris: StateFlow<List<Uri>> = _fotosSelecionadasUris

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

    fun adicionarFoto(uri: Uri) {
        _fotosSelecionadasUris.value += uri
    }

    fun limparFotos() {
        _fotosSelecionadasUris.value = emptyList()
    }
    private suspend fun uploadImagens(uris: List<Uri>): List<String> {
        val urlsDownload = mutableListOf<String>()
        val context = getApplication<Application>().applicationContext
        return withContext(Dispatchers.IO) {
            for (uri in uris) {
                val nomeArquivo = "${System.currentTimeMillis()}_${UUID.randomUUID()}.jpg"
                val ref = storage.reference.child("posts_images/$nomeArquivo")
                try {
                    val dadosDaImagem = comprimirImagem(context, uri)
                    if (dadosDaImagem != null) {
                        ref.putBytes(dadosDaImagem).await()
                        val url = ref.downloadUrl.await().toString()
                        urlsDownload.add(url)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            urlsDownload
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
            val listaDeUrls = uploadImagens(_fotosSelecionadasUris.value)

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
                urlsFotos = listaDeUrls,
                data = System.currentTimeMillis()
            )

            val resultado = repository.criarPost(novaPostagem)

            resultado.onSuccess {
                _uiState.value = PostUiState(isSuccess = true)
            }.onFailure { e ->
                _uiState.value = PostUiState(error = e.message ?: "Erro")
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

    // CURTIDAS E COMENTARIOS - JULIO EMANUEL

    fun toggleCurtidaPost(post: Postagem) {
        viewModelScope.launch {
            val meuId = auth.currentUser?.uid ?: return@launch
            val jaCurtiu = post.curtidas.contains(meuId)
            val userAtual = userRepository.getUsuarioAtual()
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
                if (post.userId==meuId) {
                    return@launch
                }
                val novaNotificacao = Notificacao(
                    destinatarioId = post.userId,
                    remetenteId = meuId,
                    remetenteNome = userAtual.nickname,
                    tipo = TipoNotificacao.CURTIDA,
                    mensagem = "${userAtual.nickname} curtiu a sua corrida!",
                    data = System.currentTimeMillis()
                )
                launch {
                    try {
                        notificationRepository.criarNotificacao(novaNotificacao)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
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
                carregarComentarios(postId)
            }
            if (remetenteId==meuId) {
                return@launch
            }
            val novaNotificacao = Notificacao(
                destinatarioId = remetenteId,
                remetenteId = meuId,
                remetenteNome = usuario.nickname,
                tipo = TipoNotificacao.COMENTARIO,
                mensagem = "${usuario.nickname} comentou na sua corrida!.",
                data = System.currentTimeMillis()
            )
            launch {
                try {
                    notificationRepository.criarNotificacao(novaNotificacao)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
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