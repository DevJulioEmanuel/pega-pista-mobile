package com.example.pegapista.data.repository

import com.example.pegapista.data.models.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val usersRef = db.collection("usuarios")

    suspend fun getUsuarioAtual(): Usuario {
        val firebaseUser = auth.currentUser ?: throw Exception("Não logado")
        val uid = firebaseUser.uid

        val snapshot = usersRef.document(uid).get().await()

        return if (snapshot.exists()) {
            snapshot.toObject(Usuario::class.java)!!.copy(id = uid)
        } else {
            val novoUsuario = Usuario(
                id = uid,
                nickname = firebaseUser.displayName ?: "Atleta PegaPista",
                email = firebaseUser.email ?: "",
                fotoPerfilUrl = firebaseUser.photoUrl?.toString()
            )
            usersRef.document(uid).set(novoUsuario).await()
            novoUsuario
        }
    }


    //BUSCAR USUARIO, TELA DE BUSCA - JULIO EMANUEL

    suspend fun buscarUsuarios(termo: String): List<Usuario> {
        if (termo.isBlank()) return emptyList()

        return try {
            val snapshot = usersRef
                .whereGreaterThanOrEqualTo("nickname", termo)
                .whereLessThanOrEqualTo("nickname", termo + "\uf8ff")
                .limit(20)
                .get()
                .await()

            snapshot.toObjects(Usuario::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getUsuariosSugestao(): List<Usuario> {
        return try {
            val snapshot = usersRef
                .limit(10)
                .get()
                .await()

            snapshot.toObjects(Usuario::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

}