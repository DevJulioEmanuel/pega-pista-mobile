package com.example.pegapista.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pegapista.data.models.Notificacao
import com.example.pegapista.data.models.Postagem
import com.example.pegapista.data.models.TipoNotificacao
import com.example.pegapista.data.models.Usuario
import com.example.pegapista.data.repository.NotificationRepository
import com.example.pegapista.data.repository.PostRepository
import com.example.pegapista.data.repository.UserRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PerfilUsuarioViewModel(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository = UserRepository()
): ViewModel() {

    private val repository = UserRepository()

    private val notificationRepository = NotificationRepository()
    private val _userState = MutableStateFlow(Usuario(nickname = "Carregando..."))
    val userState = _userState.asStateFlow()

    private val _isSeguindo = MutableStateFlow(false)
    val isSeguindo = _isSeguindo.asStateFlow()
    private val _postsUsuario = MutableStateFlow<List<Postagem>>(emptyList())
    val postsUsuario = _postsUsuario.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun carregarPerfilUsuario(userId: String) {
        if (userId.isBlank()) return

        viewModelScope.launch {
            _isLoading.value = true

            val usuarioDeferred = async { repository.getUsuarioPorId(userId) }
            val segueDeferred = async { repository.verificarSeSegue(userId) }
            val postsDeferred = async { postRepository.getPostsPorUsuario(userId) }

            val usuarioEncontrado = usuarioDeferred.await()
            val segueAtualmente = segueDeferred.await()
            val postsEncontrados = postsDeferred.await()

            if (usuarioEncontrado != null) {
                _userState.value = usuarioEncontrado
            }

            _isSeguindo.value = segueAtualmente
            _postsUsuario.value = postsEncontrados

            _isLoading.value = false
        }
    }

    fun carregarPosts(userId: String) {
        viewModelScope.launch {
            // Usa 'postRepository' (o do construtor)
            val posts = postRepository.getPostsPorUsuario(userId)
            // ...
        }
    }
    fun toggleSeguir() {
        val userAlvo = _userState.value
        if (userAlvo.id.isBlank()) return
        viewModelScope.launch {
            val userAtual = repository.getUsuarioAtual()
            val jaSegue = _isSeguindo.value
            _isSeguindo.value = !jaSegue
            if (jaSegue) {
                repository.deixarDeSeguirUsuario(userAlvo.id)
            } else {
                repository.seguirUsuario(userAlvo.id)
                val novaNotificacao = Notificacao(
                    destinatarioId = userAlvo.id,
                    remetenteId = userAtual.id,
                    remetenteNome = userAtual.nickname,
                    tipo = TipoNotificacao.SEGUIR,
                    mensagem = "${userAtual.nickname} começou a seguir você!.",
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

    fun atualizarLikeNoPostLocal(postId: String, userIdLogado: String) {
        val listaAtual = _postsUsuario.value.toMutableList()
        val index = listaAtual.indexOfFirst { it.id == postId }

        if (index != -1) {
            val postAntigo = listaAtual[index]
            val novasCurtidas = postAntigo.curtidas.toMutableList()

            if (novasCurtidas.contains(userIdLogado)) {
                novasCurtidas.remove(userIdLogado)
            } else {
                novasCurtidas.add(userIdLogado)
            }
            val postNovo = postAntigo.copy(curtidas = novasCurtidas)
            listaAtual[index] = postNovo
            _postsUsuario.value = listaAtual
        }
    }

}