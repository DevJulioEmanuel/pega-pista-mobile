package com.example.pegapista.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pegapista.data.models.Usuario
import com.example.pegapista.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val userRepository = UserRepository()

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario

    private val _ranking = MutableStateFlow<List<Usuario>>(emptyList())
    val ranking: StateFlow<List<Usuario>> = _ranking

    init {
        carregarDadosUsuario()
    }

    fun carregarDadosUsuario() {
        viewModelScope.launch {
            try {
                val user = userRepository.getUsuarioAtual()
                _usuario.value = user
                _ranking.value = userRepository.getRankingSeguindo()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
