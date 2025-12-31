package com.example.pegapista.data.repository

import android.content.Context
import androidx.work.*
import com.example.pegapista.data.models.Corrida
import com.example.pegapista.database.AppDatabase
import com.example.pegapista.database.dao.toEntity
import com.example.pegapista.worker.SyncCorridasWorker
import com.google.firebase.auth.FirebaseAuth
import java.util.UUID

class CorridaRepository(
    private val db: AppDatabase,
    private val context: Context
) {
    private val auth = FirebaseAuth.getInstance()
    private val workManager = WorkManager.getInstance(context)

    suspend fun salvarCorrida(corrida: Corrida): Result<Boolean> {
        return try {
            val user = auth.currentUser ?: throw Exception("Usuário não logado")

            val entity = corrida.toEntity().copy(
                userId = user.uid,
                sincronizado = false
            )
            db.corridaDao().salvarCorrida(entity)
            agendarSincronizacao()


            Result.success(true)
        } catch (e: Exception) {
            e.printStackTrace()
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