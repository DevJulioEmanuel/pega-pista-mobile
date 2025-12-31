package com.example.pegapista.data.repository

import android.content.Context
import androidx.work.* // Importar WorkManager
import com.example.pegapista.data.local.AppDatabase
import com.example.pegapista.data.models.Corrida
import com.example.pegapista.worker.SyncCorridasWorker
import com.google.firebase.auth.FirebaseAuth
import java.util.UUID

class CorridaRepository(private val context: Context) {

    private val dbLocal = AppDatabase.getDatabase(context)
    private val auth = FirebaseAuth.getInstance()
    private val workManager = WorkManager.getInstance(context)

    suspend fun salvarCorrida(corrida: Corrida): Result<Boolean> {
        return try {
            val user = auth.currentUser ?: throw Exception("Usuário não logado")


            val novaCorrida = corrida.copy(
                userId = user.uid,
                sincronizado = false
            )


            dbLocal.corridaDao().salvarCorrida(novaCorrida)

            agendarSincronizacao()

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun agendarSincronizacao() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<SyncCorridasWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniqueWork(
            "sync_corridas",
            ExistingWorkPolicy.KEEP,
            syncRequest
        )
    }

    fun gerarIdCorrida(): String {
        return UUID.randomUUID().toString()
    }
}