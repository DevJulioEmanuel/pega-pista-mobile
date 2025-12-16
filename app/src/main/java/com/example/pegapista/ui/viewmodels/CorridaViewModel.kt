package com.example.pegapista.ui.viewmodels

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pegapista.data.manager.LocationManager
import com.example.pegapista.data.models.Corrida
import com.example.pegapista.data.repository.CorridaRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SaveRunState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

class CorridaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CorridaRepository()
    private val locationManager = LocationManager(application)

    private val _distancia = MutableLiveData(0f)
    val distancia: LiveData<Float> = _distancia

    private val _tempoSegundos = MutableLiveData(0L)
    val tempoSegundos: LiveData<Long> = _tempoSegundos

    private val _pace = MutableLiveData("-:--") // Começa com traço
    val pace: LiveData<String> = _pace

    private val _isRastreando = MutableLiveData(false)
    val isRastreando: LiveData<Boolean> = _isRastreando

    private val _saveState = MutableStateFlow(SaveRunState())
    val saveState = _saveState.asStateFlow()

    private var timerJob: Job? = null


    fun toggleRastreamento() {
        if (_isRastreando.value == true) {
            pausarCorrida()
        } else {
            iniciarCorrida()
        }
    }

    fun iniciarCorrida() {
        if (_isRastreando.value == true) return

        _isRastreando.value = true

        locationManager.startTracking(object : LocationManager.LocationListener {
            override fun onLocationUpdate(velKmh: Double, distMetros: Float, loc: Location) {
                _distancia.postValue(distMetros)
                calcularPace(distMetros, _tempoSegundos.value ?: 0L)
            }
        })

        startTimer()
    }

    fun pausarCorrida() {
        _isRastreando.value = false
        locationManager.stopTracking()
        timerJob?.cancel()
    }


    fun finalizarESalvarCorrida() {
        pausarCorrida()

        _saveState.value = SaveRunState(isLoading = true)

        viewModelScope.launch {
            val distMetros = _distancia.value ?: 0f
            val tempoSec = _tempoSegundos.value ?: 0L
            val paceAtual = _pace.value ?: "-:--"

            val novoId = repository.gerarIdCorrida()

            val novaCorrida = Corrida(
                id = novoId,
                distanciaKm = (distMetros / 1000).toDouble(),
                tempo = formatarTempoParaString(tempoSec),
                pace = paceAtual
            )

            val resultado = repository.salvarCorrida(novaCorrida)

            resultado.onSuccess {
                _saveState.value = SaveRunState(isSuccess = true)
            }.onFailure { e ->
                _saveState.value = SaveRunState(error = e.message ?: "Erro desconhecido")
            }
        }
    }


    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_isRastreando.value == true) {
                delay(1000)
                val novoTempo = (_tempoSegundos.value ?: 0L) + 1
                _tempoSegundos.postValue(novoTempo)

                calcularPace(_distancia.value ?: 0f, novoTempo)
            }
        }
    }

    private fun calcularPace(distanciaMetros: Float, segundos: Long) {
        if (distanciaMetros < 50) {
            _pace.postValue("-:--")
            return
        }

        val distanciaKm = distanciaMetros / 1000.0
        val tempoMinutos = segundos / 60.0

        if (distanciaKm > 0) {
            val paceDec = tempoMinutos / distanciaKm
            val minutos = paceDec.toInt()
            val segundosRestantes = ((paceDec - minutos) * 60).toInt()

            if (minutos < 60) {
                _pace.postValue("%d:%02d".format(minutos, segundosRestantes))
            }
        }
    }

    fun formatarTempoParaString(segundos: Long): String {
        val horas = segundos / 3600
        val minutos = (segundos % 3600) / 60
        val segs = segundos % 60
        return if (horas > 0) "%d:%02d:%02d".format(horas, minutos, segs)
        else "%02d:%02d".format(minutos, segs)
    }

    override fun onCleared() {
        super.onCleared()
        locationManager.stopTracking()
        timerJob?.cancel()
    }
}