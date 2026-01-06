package com.example.pegapista.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pegapista.data.models.Postagem
import com.example.pegapista.data.models.Usuario
import com.example.pegapista.data.repository.UserRepository
import com.example.pegapista.data.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val userRepository = UserRepository()
    private val postRepository = PostRepository()

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario

    private val _ranking = MutableStateFlow<List<Usuario>>(emptyList())
    val ranking: StateFlow<List<Usuario>> = _ranking

    private val _atividadesAmigos = MutableStateFlow<List<Postagem>>(emptyList())
    val atividadesAmigos: StateFlow<List<Postagem>> = _atividadesAmigos

    init {
        carregarDadosUsuario()
    }

    fun carregarDadosUsuario() {
        viewModelScope.launch {
            try {
                val user = userRepository.getUsuarioAtual()
                _usuario.value = user
                val idsSeguindo = userRepository.getIdsSeguindo()
                _ranking.value = userRepository.getRankingSeguindo()
                if (idsSeguindo.isNotEmpty()) {
                    _atividadesAmigos.value = postRepository.getFeedPosts(idsSeguindo).take(5)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
