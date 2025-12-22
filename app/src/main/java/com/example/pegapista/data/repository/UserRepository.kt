package com.example.pegapista.data.repository

import com.example.pegapista.data.models.Usuario
import com.example.pegapista.utils.DateUtils
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

    suspend fun atualizarSequenciaDiaria() {
        val uid = auth.currentUser?.uid ?: return
        val snapshot = usersRef.document(uid).get().await()
        val usuario = snapshot.toObject(Usuario::class.java) ?: return

        val agora = System.currentTimeMillis()
        val ultimaAtiv = usuario.ultimaAtividade

        // 1. Se já fez algo HOJE, não aumenta a sequência de novo
        if (DateUtils.isMesmoDia(agora, ultimaAtiv)) return

        // 2. Define a nova sequência
        val novaSequencia = if (DateUtils.isOntem(ultimaAtiv)) {
            usuario.diasSeguidos + 1 // Ontem ele fez, então é +1
        } else {
            1 // Não fez ontem, então reseta ou inicia em 1
        }

        // Verifica recorde
        val novoRecorde = if (novaSequencia > usuario.recordeDiasSeguidos) {
            novaSequencia
        } else {
            usuario.recordeDiasSeguidos
        }

        // Salva no Firestore
        usersRef.document(uid).update(
            mapOf(
                "diasSeguidos" to novaSequencia,
                "recordeDiasSeguidos" to novoRecorde,
                "ultimaAtividade" to agora
            )
        ).await()
    }
}