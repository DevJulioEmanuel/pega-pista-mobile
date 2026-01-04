package com.example.pegapista.ui.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pegapista.data.models.Usuario
import com.example.pegapista.data.repository.AuthRepository
import com.example.pegapista.data.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import comprimirImagem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class PerfilViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val repository = UserRepository()

    private val _userState = MutableStateFlow(Usuario(nickname = "Carregando..."))
    val userState = _userState.asStateFlow()

    private val _isLoadingFoto = MutableStateFlow(false)
    val isLoadingFoto = _isLoadingFoto.asStateFlow()

    init {
        carregarPerfil()
    }

    fun deslogar() {
        authRepository.signOut()
    }
    fun carregarPerfil() {
        viewModelScope.launch {
            try {
                val usuario = repository.getUsuarioAtual()
                _userState.value = usuario
            } catch (e: Exception) {
                Log.e("PERFIL_VM", "Erro ao carregar perfil: ${e.message}")
            }
        }
    }

    fun atualizarFotoPerfil(uriImagem: Uri, context: Context) {
        viewModelScope.launch {
            val usuarioAtual = _userState.value
            if (usuarioAtual.id.isEmpty()) return@launch
            _isLoadingFoto.value = true

            try {
                val dadosDaFoto = comprimirImagem(context, uriImagem)
                val storageRef = FirebaseStorage.getInstance().reference
                val nomeArquivo = "${usuarioAtual.id}/${UUID.randomUUID()}.jpg"
                val fotoRef = storageRef.child("fotos_perfil/$nomeArquivo")

                fotoRef.putBytes(dadosDaFoto).await()
                val downloadUrl = fotoRef.downloadUrl.await().toString()

                FirebaseFirestore.getInstance()
                    .collection("usuarios")
                    .document(usuarioAtual.id)
                    .update("fotoPerfilUrl", downloadUrl)
                    .await()

                _userState.value = usuarioAtual.copy(fotoPerfilUrl = downloadUrl)

            } catch (e: Exception) {
                Log.e("PERFIL_VM", "Erro ao atualizar foto: ${e.message}")
            } finally {
                _isLoadingFoto.value = false
            }
        }
    }
}