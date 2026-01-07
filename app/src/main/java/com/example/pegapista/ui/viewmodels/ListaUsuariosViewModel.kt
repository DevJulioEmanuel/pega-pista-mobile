package com.example.pegapista.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pegapista.data.models.Usuario
import com.example.pegapista.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ListaUsuariosViewModel : ViewModel() {
    private val repository = UserRepository()

    private val _listaUsuarios = MutableStateFlow<List<Usuario>>(emptyList())
    val listaUsuarios = _listaUsuarios.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun carregarLista(userId: String, tipo: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val resultado = if (tipo == "SEGUIDORES") {
                    repository.getListaSeguidores(userId)
                } else {
                    repository.getListaSeguindo(userId)
                }
                _listaUsuarios.value = resultado
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}