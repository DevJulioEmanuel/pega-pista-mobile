package com.example.pegapista.ui.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pegapista.data.models.Usuario
import com.example.pegapista.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ATENÇÃO: O nome da classe aqui é PerfilViewModel
class PerfilViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    // Estado do Usuário (para a tela observar os dados)
    private val _userState = MutableStateFlow<Usuario?>(null)
    val userState = _userState.asStateFlow()

    init {
        carregarPerfil()
    }

    // Carrega os dados do utilizador logado
    fun carregarPerfil() {
        viewModelScope.launch {
            try {
                val usuario = userRepository.getUsuarioAtual()
                _userState.value = usuario
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Função que a tela PerfilScreen está pedindo
    fun deslogar() {
        auth.signOut()
        // A navegação na tela vai perceber que o user é null e voltar pro login
    }

    // Função que a tela PerfilScreen está pedindo
    fun atualizarFotoPerfil(uri: Uri) {
        viewModelScope.launch {
            try {
                // Aqui chamamos o repositório (assumindo que tens essa função lá,
                // senão podes deixar comentado por enquanto)
                // userRepository.atualizarFoto(uri)

                // Recarrega o perfil para mostrar a foto nova
                carregarPerfil()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}