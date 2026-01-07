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
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.pegapista.database.AppDatabase

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val postRepository = PostRepository(db, application)
    private val userRepository = UserRepository()

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
                _ranking.value = userRepository.getRankingSeguindo()
                val idsSeguindo = userRepository.getIdsSeguindo().toMutableList()
                if (user.id.isNotEmpty()) {
                    idsSeguindo.add(user.id)
                }
                if (idsSeguindo.isNotEmpty()) {
                    val listaIdsSegura = idsSeguindo.take(10)
                    _atividadesAmigos.value = postRepository.getFeedPosts(listaIdsSegura).take(5)
                } else {
                    _atividadesAmigos.value = emptyList()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
