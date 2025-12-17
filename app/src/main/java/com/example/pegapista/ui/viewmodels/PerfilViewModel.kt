package com.example.pegapista.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pegapista.data.models.Usuario
import com.example.pegapista.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PerfilViewModel : ViewModel() {
    private val repository = UserRepository()

    private val _userState = MutableStateFlow(Usuario(nickname = "Carregando..."))
    val userState = _userState.asStateFlow()

    init {
        carregarPerfil()
    }

    fun carregarPerfil() {
        viewModelScope.launch {
            try {
                val usuario = repository.getUsuarioAtual()

                android.util.Log.d("DEBUG_PERFIL", "Usuário carregado: ${usuario.nickname} - ID: ${usuario.id}")

                _userState.value = usuario
            } catch (e: Exception) {
                android.util.Log.e("DEBUG_PERFIL", "Erro: ${e.message}")
            }
        }
    }
}