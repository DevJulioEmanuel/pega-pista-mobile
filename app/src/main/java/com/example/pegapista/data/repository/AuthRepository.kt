package com.example.pegapista.data.repository

import com.example.pegapista.data.models.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await


class AuthRepository {

    private val auth = FirebaseAuth.getInstance()

    fun signOut() {
        auth.signOut()
    }
    private val db = FirebaseFirestore.getInstance()

    suspend fun login(email: String, senha: String): Result<Boolean> {
        return try {
            auth.signInWithEmailAndPassword(email, senha).await()
            salvarFcmToken()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cadastrar(nome: String, email: String, senha: String): Result<Boolean> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, senha).await()
            val user = authResult.user ?: throw Exception("Erro ao criar usuário")

            val novoUsuario = Usuario(
                id = user.uid,
                nickname = nome,
                email = email,
            )

            db.collection("usuarios").document(user.uid).set(novoUsuario).await()
            salvarFcmToken()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //GOOGLE

    suspend fun loginComGoogle(idToken: String): Result<Boolean> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()

            val user = authResult.user ?: throw Exception("Usuário nulo")

            val userRef = db.collection("usuarios").document(user.uid)
            val snap = userRef.get().await()
            if (!snap.exists()) {
                val novoUsuario = Usuario(
                    id = user.uid,
                    nickname = user.displayName ?: "Usuário",
                    email = user.email ?: ""
                )
                userRef.set(novoUsuario).await()
            }
            salvarFcmToken()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun salvarFcmToken() {
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@addOnSuccessListener

                FirebaseFirestore.getInstance()
                    .collection("usuarios")
                    .document(uid)
                    .set(
                        mapOf("fcmToken" to token),
                        SetOptions.merge()
                    )
            }
    }


    fun getCurrentUser() = auth.currentUser
}