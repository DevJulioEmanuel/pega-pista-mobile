package com.example.pegapista.data.repository

import com.example.pegapista.data.models.Usuario
import com.example.pegapista.utils.DateUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
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

    suspend fun getUsuarioPorId(userId: String): Usuario? {
        return try {
            val snapshot = usersRef.document(userId).get().await()
            if (snapshot.exists()) {
                snapshot.toObject(Usuario::class.java)?.copy(id = userId)
            } else {
                null
            }
        } catch (e: Exception) {
            null
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


    // LOGICA DE SEGUIR (RELACIONAMENTOS) - JULIO EMANUEL

    suspend fun seguirUsuario(idAmigo: String): Boolean {
        val meuId = auth.currentUser?.uid ?: return false
        return try {
            val batch = db.batch()

            val relacaoRef = db.collection("usuarios").document(meuId)
                .collection("seguindo").document(idAmigo)
            batch.set(relacaoRef, mapOf("data" to System.currentTimeMillis()))

            val meuPerfilRef = db.collection("usuarios").document(meuId)
            batch.update(meuPerfilRef, "seguindo", FieldValue.increment(1))

            val amigoRef = db.collection("usuarios").document(idAmigo)
            batch.update(amigoRef, "seguidores", FieldValue.increment(1))

            batch.commit().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deixarDeSeguirUsuario(idAmigo: String): Boolean {
        val meuId = auth.currentUser?.uid ?: return false
        return try {
            val batch = db.batch()

            val relacaoRef = db.collection("usuarios").document(meuId)
                .collection("seguindo").document(idAmigo)
            batch.delete(relacaoRef)

            val meuPerfilRef = db.collection("usuarios").document(meuId)
            batch.update(meuPerfilRef, "seguindo", FieldValue.increment(-1))

            val amigoRef = db.collection("usuarios").document(idAmigo)
            batch.update(amigoRef, "seguidores", FieldValue.increment(-1))

            batch.commit().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun verificarSeSegue(idAmigo: String): Boolean {
        val meuId = auth.currentUser?.uid ?: return false
        return try {
            val doc = db.collection("usuarios").document(meuId)
                .collection("seguindo").document(idAmigo)
                .get()
                .await()
            doc.exists()
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getIdsSeguindo(): List<String> {
        val meuId = auth.currentUser?.uid ?: return emptyList()

        return try {
            val snapshot = db.collection("usuarios").document(meuId)
                .collection("seguindo")
                .get()
                .await()
            snapshot.documents.map { it.id }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getRankingSeguindo(): List<Usuario> {
        return try {
            val idsSeguindo = getIdsSeguindo()
            val meuId = auth.currentUser?.uid ?: return emptyList()

            val todosIds = idsSeguindo.toMutableList().apply { add(meuId) }

            if (todosIds.isEmpty()) return emptyList()

            // Se tiver mais que 10 amigos, precisará fazer múltiplas consultas ou mudar a lógica.
            val snapshot = usersRef
                .whereIn("id", todosIds)
                .get()
                .await()

            snapshot.toObjects(Usuario::class.java)
                .sortedByDescending { it.diasSeguidos } // Ordena pelo foguinho
        } catch (e: Exception) {
            emptyList()
        }
    }



}