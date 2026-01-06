package com.example.pegapista.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.pegapista.R
import com.example.pegapista.data.manager.LocationManager
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow

class RunningService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private lateinit var locationManager: LocationManager
    private val notificationId = 1

    override fun onCreate() {
        super.onCreate()
        locationManager = LocationManager(this)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "START" -> startRun()
            "PAUSE" -> pauseRun()
            "STOP" -> stopRun()
        }
        return START_STICKY
    }

    private fun startRun() {
        if (RunningState.isRastreando.value) return

        RunningState.isRastreando.value = true

        startForeground(notificationId, buildNotification())

        locationManager.startTracking(object : LocationManager.LocationListener {
            override fun onLocationUpdate(velKmh: Double, distMetros: Float, loc: Location) {
                RunningState.distanciaMetros.value = distMetros

                val novoPonto = LatLng(loc.latitude, loc.longitude)
                val listaAtual = RunningState.percurso.value.toMutableList()
                listaAtual.add(novoPonto)
                RunningState.percurso.value = listaAtual

                updateNotification("Distância: %.2f km".format(distMetros / 1000))
            }
        })

        serviceScope.launch {
            while (RunningState.isRastreando.value) {
                delay(1000L)
                val novoTempo = RunningState.tempoSegundos.value + 1
                RunningState.tempoSegundos.value = novoTempo

                calcularPace(RunningState.distanciaMetros.value, novoTempo)
            }
        }
    }

    private fun pauseRun() {
        RunningState.isRastreando.value = false
        locationManager.stopTracking()
        updateNotification("Corrida Pausada")
    }

    private fun stopRun() {
        pauseRun()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun calcularPace(distanciaMetros: Float, segundos: Long) {
        if (distanciaMetros < 50) return
        val distKm = distanciaMetros / 1000.0
        val tempoMin = segundos / 60.0
        if (distKm > 0) {
            val paceDec = tempoMin / distKm
            val min = paceDec.toInt()
            val sec = ((paceDec - min) * 60).toInt()
            if (min < 60) RunningState.pace.value = "%d:%02d".format(min, sec)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "running_channel",
                "Rastreamento de Corrida",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): android.app.Notification {
        return NotificationCompat.Builder(this, "running_channel")
            .setContentTitle("PegaPista Ativo")
            .setContentText("Rastreando sua corrida...")
            .setSmallIcon(R.drawable.ic_notification)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(texto: String) {
        val notification = NotificationCompat.Builder(this, "running_channel")
            .setContentTitle("PegaPista Ativo")
            .setContentText(texto)
            .setSmallIcon(R.drawable.ic_notification)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()

        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(notificationId, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}