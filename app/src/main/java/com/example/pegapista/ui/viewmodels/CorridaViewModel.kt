package com.example.pegapista.ui.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pegapista.data.manager.LocationManager
import com.example.pegapista.data.models.Corrida
import com.example.pegapista.data.repository.CorridaRepository
import com.example.pegapista.service.RunningService
import com.example.pegapista.service.RunningState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class SaveRunState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

class CorridaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CorridaRepository()
    private val _saveState = MutableStateFlow(SaveRunState())
    val saveState = _saveState.asStateFlow()

    val distancia = RunningState.distanciaMetros
    val tempoSegundos = RunningState.tempoSegundos
    val pace = RunningState.pace
    val isRastreando = RunningState.isRastreando
    val percurso = RunningState.percurso

    //Para a camera
    private val _localizacaoInicial = MutableStateFlow<LatLng?>(null)
    val localizacaoInicial: StateFlow<LatLng?> = _localizacaoInicial

    init {
        buscarUltimaLocalizacao()
    }

    fun toggleRastreamento() {
        val intent = Intent(getApplication(), RunningService::class.java)
        if (isRastreando.value) {
            intent.action = "PAUSE"
        } else {
            intent.action = "START"
        }
        getApplication<Application>().startService(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun iniciarCorrida() {
        RunningState.tempoSegundos.value = 0L
        RunningState.distanciaMetros.value = 0f
        RunningState.pace.value = "-:--"
        RunningState.percurso.value = emptyList()

        val intent = Intent(getApplication(), RunningService::class.java)
        intent.action = "START"
        getApplication<Application>().startForegroundService(intent)
    }

    fun finalizarESalvarCorrida() {
        val intent = Intent(getApplication(), RunningService::class.java)
        intent.action = "STOP"
        getApplication<Application>().startService(intent)

        _saveState.value = SaveRunState(isLoading = true)

        viewModelScope.launch {
            val distFinal = RunningState.distanciaMetros.value
            val tempoFinal = RunningState.tempoSegundos.value
            val paceFinal = RunningState.pace.value

            val novaCorrida = Corrida(
                id = repository.gerarIdCorrida(),
                distanciaKm = (distFinal / 1000).toDouble(),
                tempo = formatarTempoParaString(tempoFinal),
                pace = paceFinal
            )

            repository.salvarCorrida(novaCorrida).onSuccess {
                _saveState.value = SaveRunState(isSuccess = true)
            }.onFailure {
                _saveState.value = SaveRunState(error = it.message)
            }
        }
    }

    fun finalizarCorrida() {
        val intent = Intent(getApplication(), RunningService::class.java)
        intent.action = "STOP"
        getApplication<Application>().startService(intent)
    }

    fun formatarTempoParaString(segundos: Long): String {
        val horas = segundos / 3600
        val minutos = (segundos % 3600) / 60
        val segs = segundos % 60
        return if (horas > 0) "%d:%02d:%02d".format(horas, minutos, segs)
        else "%02d:%02d".format(minutos, segs)
    }

    @SuppressLint("MissingPermission") // Assumimos que a MainActivity já pediu permissão
    fun buscarUltimaLocalizacao() {
        val context = getApplication<Application>().applicationContext
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        viewModelScope.launch {
            try {
                val location = fusedLocationClient.lastLocation.await()
                if (location != null) {
                    _localizacaoInicial.value = LatLng(location.latitude, location.longitude)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}