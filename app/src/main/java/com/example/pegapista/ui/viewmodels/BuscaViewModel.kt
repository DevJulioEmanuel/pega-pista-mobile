package com.example.pegapista.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pegapista.data.models.Usuario
import com.example.pegapista.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BuscaViewModel : ViewModel() {
    private val repository = UserRepository()

    private val _textoBusca = MutableStateFlow("")
    val textoBusca = _textoBusca.asStateFlow()

    private val _resultados = MutableStateFlow<List<Usuario>>(emptyList())
    val resultados = _resultados.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        carregarSugestoes()
    }
    fun atualizarBusca(novoTexto: String) {
        _textoBusca.value = novoTexto

        if (novoTexto.isBlank()) {
            _resultados.value = emptyList()
            return
        }

        performarBusca(novoTexto)
    }

    private fun performarBusca(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val lista = repository.buscarUsuarios(query)
            _resultados.value = lista
            _isLoading.value = false
        }
    }

    private fun carregarSugestoes() {
        viewModelScope.launch {
            _isLoading.value = true
            val lista = repository.getUsuariosSugestao()
            _resultados.value = lista
            _isLoading.value = false
        }
    }

    suspend fun getFotoPerfil(userId: String): String? {
        return try {
            repository.getUsuarioPorId(userId)?.fotoPerfilUrl
        } catch (e: Exception) {
            null
        }
    }
}