package com.example.pegapista.database.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.pegapista.data.models.Usuario
import com.example.pegapista.database.AppDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.tasks.await

class SyncUsersWorker(context: Context, params: WorkerParameters): CoroutineWorker(context,params) {
    override suspend fun doWork(): Result {
        val dao = AppDatabase.getDatabase(applicationContext).userDao()
        val usersNaoSincronizados = dao.getUsernaoSincronizados()
        val dbFirestore = FirebaseFirestore.getInstance()
        return try {
            // Itera sobre a lista simples
            usersNaoSincronizados.forEach { userEntity ->

                dbFirestore.collection("users")
                    .document(userEntity.id)
                    .set(userEntity)
                    .await() // .await() funciona bem aqui porque doWork é suspend

                // Atualiza localmente para sincronizado
                dao.atualizarUser(userEntity.copy(userSincronizado = true))
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}