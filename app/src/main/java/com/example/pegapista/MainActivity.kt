package com.example.pegapista

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.pegapista.ui.theme.PegaPistaTheme
import com.example.pegapista.utils.CHANNEL_ID
import com.example.pegapista.utils.showNotification
import com.example.pegapista.worker.LembreteWorker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        createNotificationChannel()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
        agendarNotificacaoDiaria()
        setContent {
            PegaPistaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PegaPistaScreen()
                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notificações PegaPista"
            val descriptionText = "Canal para avisos de corridas, redes sociais e conquistas"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun agendarNotificacaoDiaria() {
        val horaAlvo = 8
        val minutoAlvo = 0

        val agora = Calendar.getInstance()
        val horarioNotificacao = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, horaAlvo)
            set(Calendar.MINUTE, minutoAlvo)
            set(Calendar.SECOND, 0)
        }

        if (horarioNotificacao.before(agora)) {
            horarioNotificacao.add(Calendar.DAY_OF_MONTH, 1)
        }

        val tempoAteInicio = horarioNotificacao.timeInMillis - agora.timeInMillis

        val lembreteRequest = PeriodicWorkRequestBuilder<LembreteWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(tempoAteInicio, TimeUnit.MILLISECONDS)
            .addTag("lembrete_diario")
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "WorkNotificacaoDiaria",
            ExistingPeriodicWorkPolicy.KEEP,
            lembreteRequest
        )
    }
}