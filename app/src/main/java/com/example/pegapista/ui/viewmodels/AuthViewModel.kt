package com.example.pegapista.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pegapista.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.google.firebase.auth.GoogleAuthProvider


// Estado da UI para autenticação
data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun resetState() {
        _uiState.value = AuthUiState()
    }

    fun login(email: String, senha: String) {
        if (email.isBlank() || senha.isBlank()) {
            _uiState.value = AuthUiState(error = "Preencha todos os campos")
            return
        }

        _uiState.value = AuthUiState(isLoading = true)

        viewModelScope.launch {
            val result = repository.login(email, senha)
            result.onSuccess {
                _uiState.value = AuthUiState(isSuccess = true)
            }.onFailure { exception ->
                _uiState.value = AuthUiState(error = exception.message ?: "Erro desconhecido")
            }
        }
    }

    fun cadastrar(nome: String, email: String, senha: String, confirmarSenha: String) {
        if (nome.isBlank() || email.isBlank() || senha.isBlank()) {
            _uiState.value = AuthUiState(error = "Preencha todos os campos")
            return
        }
        if (senha != confirmarSenha) {
            _uiState.value = AuthUiState(error = "As senhas não coincidem")
            return
        }
        if (senha.length < 6) {
            _uiState.value = AuthUiState(error = "A senha deve ter pelo menos 6 caracteres")
            return
        }

        _uiState.value = AuthUiState(isLoading = true)

        viewModelScope.launch {
            val result = repository.cadastrar(nome, email, senha)
            result.onSuccess {
                _uiState.value = AuthUiState(isSuccess = true)
            }.onFailure { exception ->
                _uiState.value = AuthUiState(error = exception.message ?: "Erro ao cadastrar")
            }
        }
    }

    //GOOGLE

    fun loginComGoogle(idToken: String) {
        _uiState.value = AuthUiState(isLoading = true)

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val auth = FirebaseAuth.getInstance()

        auth.signInWithCredential(credential)
            .addOnSuccessListener { result ->
                _uiState.value = AuthUiState(isSuccess = true)
            }
            .addOnFailureListener { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Erro no Google: ${e.message}"
                )
            }
    }
}