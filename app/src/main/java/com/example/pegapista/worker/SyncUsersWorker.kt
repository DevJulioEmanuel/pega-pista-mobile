package com.example.pegapista.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.pegapista.data.local.AppDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class SyncUsersWorker(context: Context, params: WorkerParameters): CoroutineWorker(context,params) {
    override suspend fun doWork(): Result {
        val dao = AppDatabase.getDatabase(applicationContext).userDao()
        val usersNaoSincronizados = dao.getUsernaoSincronizados()
        val dbFirestore = FirebaseFirestore.getInstance()
        return try {
            usersNaoSincronizados.forEach { userEntity ->
                dbFirestore.collection("users")
                    .document(userEntity.id)
                    .set(userEntity)
                    .await()
                dao.atualizarUser(userEntity.copy(userSincronizado = true))
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}