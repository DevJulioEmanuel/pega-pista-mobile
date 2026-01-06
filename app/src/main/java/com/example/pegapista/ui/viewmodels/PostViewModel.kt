package com.example.pegapista.ui.viewmodels

import android.net.Uri
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
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PostUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

// NOTA: Recebemos o repository pelo construtor (Injeção via Koin)
class PostViewModel(
    private val repository: PostRepository,
    // Os outros repositórios, por enquanto, podes manter instanciados aqui ou injetar depois
    private val userRepository: UserRepository = UserRepository(),
    private val notificationRepository: NotificationRepository = NotificationRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostUiState())
    val uiState = _uiState.asStateFlow()

    // ESTADO - FEED
    private val _feedState = MutableStateFlow<List<Postagem>>(emptyList())
    val feedState = _feedState.asStateFlow()

    // ESTADO - COMENTARIOS
    private val _comentariosState = MutableStateFlow<List<Comentario>>(emptyList())
    val comentariosState = _comentariosState.asStateFlow()

    private val auth = FirebaseAuth.getInstance()
    val meuId: String
        get() = auth.currentUser?.uid ?: ""

    // IMAGENS (Offline First: guardamos Uris locais)
    private val _fotosSelecionadasUris = MutableStateFlow<List<Uri>>(emptyList())
    val fotosSelecionadasUris: StateFlow<List<Uri>> = _fotosSelecionadasUris

    init {
        carregarFeed()
    }

    // --- MANIPULAÇÃO DE FOTOS ---
    fun adicionarFoto(uri: Uri) {
        _fotosSelecionadasUris.value += uri
    }

    fun limparFotos() {
        _fotosSelecionadasUris.value = emptyList()
    }

    // --- FUNÇÃO PRINCIPAL: POSTAR ---
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

                // 1. OFFLINE FIRST: Não fazemos upload agora.
                // Pegamos as URIs locais e convertemos para String para salvar no banco local.
                // O WorkManager vai pegar esses caminhos depois e fazer o upload real.
                val listaCaminhosLocais = _fotosSelecionadasUris.value.map { it.toString() }

                val corridaDados = Corrida(
                    distanciaKm = distancia,
                    tempo = tempo,
                    pace = pace
                )

                val novaPostagem = Postagem(
                    id = repository.gerarIdPost(), // Gera um ID único
                    autorNome = usuarioAtual.nickname,
                    userId = usuarioAtual.id,
                    titulo = titulo,
                    descricao = descricao,
                    corrida = corridaDados,
                    urlsFotos = listaCaminhosLocais, // SALVA O CAMINHO LOCAL (content://...)
                    data = System.currentTimeMillis()
                )

                // 2. Chama o Repository (que salva no Room e agenda o WorkManager)
                val resultado = repository.criarPost(novaPostagem)

                resultado.onSuccess {
                    _uiState.value = PostUiState(isSuccess = true)
                    limparFotos() // Limpa seleção após sucesso
                }.onFailure { e ->
                    _uiState.value = PostUiState(error = e.message ?: "Erro desconhecido")
                }
            } catch (e: Exception) {
                _uiState.value = PostUiState(error = e.message ?: "Erro ao criar post")
            }
        }
    }

    // --- FEED E LEITURA (Pode manter online por enquanto) ---
    fun carregarFeed() {
        viewModelScope.launch {
            val uid = auth.currentUser?.uid ?: return@launch

            // Lógica antiga mantida: busca amigos e carrega feed do Firebase
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
            // Aqui podes melhorar depois para apagar do Room também
            val resultado = repository.excluirPost(postId)
            resultado.onSuccess {
                carregarFeed()
                _uiState.value = PostUiState(isSuccess = true)
            }.onFailure { e ->
                _uiState.value = PostUiState(error = "Erro ao excluir: ${e.message}")
            }
        }
    }

    // --- CURTIDAS E COMENTÁRIOS (Lógica mantida) ---
    // Nota: Esta parte continua funcionando online.
    // Para funcionar offline, terias de criar tabelas locais para curtidas/comentários também,
    // mas sugiro fazer isso num passo futuro para não complicar agora.

    fun toggleCurtidaPost(post: Postagem) {
        viewModelScope.launch {
            val uid = meuId.ifEmpty { return@launch }
            val jaCurtiu = post.curtidas.contains(uid)
            val userAtual = userRepository.getUsuarioAtual() // Cuidado: isto faz chamada de rede se não tiver cache

            val sucesso = repository.toggleCurtida(post.id, uid, jaCurtiu)

            if (sucesso) {
                // Atualiza UI localmente para parecer rápido
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

                // Envia notificação apenas se for curtida (não descurtida) e não for o próprio post
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
                // Se não for o próprio dono do post, notifica
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
}