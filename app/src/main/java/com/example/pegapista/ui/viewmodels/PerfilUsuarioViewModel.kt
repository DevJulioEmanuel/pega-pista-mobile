package com.example.pegapista.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pegapista.data.models.Postagem
import com.example.pegapista.data.models.Usuario
import com.example.pegapista.data.repository.PostRepository
import com.example.pegapista.data.repository.UserRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PerfilUsuarioViewModel: ViewModel() {

    private val repository = UserRepository()
    private val postRepository = PostRepository()
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


    fun toggleSeguir() {
        val idAmigo = _userState.value.id
        if (idAmigo.isBlank()) return
        viewModelScope.launch {
            val jaSegue = _isSeguindo.value
            _isSeguindo.value = !jaSegue
            if (jaSegue) {
                repository.deixarDeSeguirUsuario(idAmigo)
            } else {
                repository.seguirUsuario(idAmigo)
            }
        }
    }

}