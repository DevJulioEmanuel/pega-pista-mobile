package com.example.pegapista.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.pegapista.database.AppDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class SyncCorridasWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val dao = AppDatabase.getDatabase(applicationContext).corridaDao()
        val naoSincronizadas = dao.getCorridasNaoSincronizadas()
        val dbFirestore = FirebaseFirestore.getInstance()

        return try {
            naoSincronizadas.forEach { corrida ->

                dbFirestore.collection("corridas")
                    .document(corrida.id)
                    .set(corrida)
                    .await()


                dao.atualizarCorrida(corrida.copy(sincronizado = true))
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}