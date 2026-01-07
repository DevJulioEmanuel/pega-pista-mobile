package com.example.pegapista.worker

import android.content.Context
import android.net.Uri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.pegapista.database.AppDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File

class SyncPostagemWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val db = AppDatabase.getDatabase(context)
    private val postagemDao = db.postagemDao()
    private val remoteDb = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    override suspend fun doWork(): Result {
        return try {
            val postsNaoSincronizados = postagemDao.getPostagemNaoSincronizada()

            if (postsNaoSincronizados.isEmpty()) {
                return Result.success()
            }

            for (entity in postsNaoSincronizados) {

                val urlsNaNuvem = mutableListOf<String>()

                val listaCaminhosLocais = if (entity.fotoUrl.isNullOrBlank()) {
                    emptyList()
                } else {
                    entity.fotoUrl.split(";")
                }

                for (caminhoLocal in listaCaminhosLocais) {
                    try {
                        val arquivo = File(caminhoLocal)
                        if (arquivo.exists()) {
                            val uriArquivo = Uri.fromFile(arquivo)
                            val storageRef = storage.reference.child("posts/${entity.id}/${arquivo.name}")


                            storageRef.putFile(uriArquivo).await()

                            val downloadUrl = storageRef.downloadUrl.await().toString()

                            urlsNaNuvem.add(downloadUrl)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()

                    }
                }

                val postHashMap = hashMapOf(
                    "id" to entity.id,
                    "userId" to entity.userId,
                    "autorNome" to entity.autorNome,
                    "titulo" to entity.titulo,
                    "descricao" to entity.descricao,
                    "corrida" to hashMapOf(
                        "distanciaKm" to entity.distanciaKm,
                        "tempo" to entity.tempo,
                        "pace" to entity.pace
                    ),
                    "data" to entity.data,
                    "curtidas" to emptyList<String>(),
                    "qtdComentarios" to 0,

                    "urlsFotos" to urlsNaNuvem
                )


                remoteDb.collection("posts").document(entity.id)
                    .set(postHashMap)
                    .await()


                val postAtualizado = entity.copy(postsincronizado = true)
                postagemDao.salvarPostagem(postAtualizado)
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}