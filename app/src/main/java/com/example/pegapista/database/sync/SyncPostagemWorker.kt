package com.example.pegapista.worker

import android.content.Context
import android.net.Uri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.core.net.toUri
import com.example.pegapista.database.AppDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class SyncPostagemWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val dao = AppDatabase.getDatabase(applicationContext).postagemDao()
        val naoSincronizadas = dao.getPostagemNaoSincronizada() // Retorna List<PostagemEntity>

        val dbFirestore = FirebaseFirestore.getInstance()
        val storageRef = FirebaseStorage.getInstance().reference

        return try {
            naoSincronizadas.forEach { postEntity ->

                var urlFinalDaFoto = postEntity.fotoUrl

                // 1. Verifica se existe foto e se ela é local (não começa com http)
                if (postEntity.fotoUrl != null && !postEntity.fotoUrl.startsWith("http")) {
                    try {
                        val uriLocal = postEntity.fotoUrl.toUri()
                        val refImagem = storageRef.child("posts/${postEntity.id}.jpg")

                        // Faz o Upload
                        refImagem.putFile(uriLocal).await()

                        // Pega o Link de Download (http...)
                        urlFinalDaFoto = refImagem.downloadUrl.await().toString()

                    } catch (e: Exception) {
                        // Se falhar o upload da imagem, não podemos enviar o post quebrado.
                        // Retornamos Retry para tentar de novo quando a net estiver melhor.
                        return Result.retry()
                    }
                }

                // 2. Prepara o objeto final para o Firestore com a URL certa
                val postParaSalvar = postEntity.copy(
                    fotoUrl = urlFinalDaFoto,
                    postsincronizado = true // No Firebase não precisamos deste campo, mas para converter ajuda
                )

                // Removemos o campo 'postsincronizado' antes de enviar, se quiseres ser purista,
                // ou enviamos tudo. Vou enviar tudo por simplicidade, mas o ideal é mapear para Model.

                dbFirestore.collection("posts")
                    .document(postEntity.id)
                    .set(postParaSalvar) // O Firestore vai salvar a estrutura do Entity
                    .await()

                // 3. Atualiza no Banco Local (Dizendo que já foi e atualizando a URL para a remota)
                dao.atualizarPostagem(postEntity.copy(
                    fotoUrl = urlFinalDaFoto, // Atualizamos localmente para a URL web também
                    postsincronizado = true
                ))
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}