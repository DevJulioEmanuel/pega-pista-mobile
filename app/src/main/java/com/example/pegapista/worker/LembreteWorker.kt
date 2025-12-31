package com.example.pegapista.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.pegapista.utils.showNotification

class NotificacaoWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val mensagens = listOf(
            "Não perca o foco! Que tal uma corrida hoje?",
            "Sua meta está te esperando. Vamos correr?",
            "O dia está ótimo para bater seus recordes!",
            "Lembre-se: constância é a chave do sucesso."
        )

        val mensagemAleatoria = mensagens.random()

        try {
            showNotification(
                applicationContext,
                "Hora de se mexer! 🏃",
                mensagemAleatoria
            )
            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }
}