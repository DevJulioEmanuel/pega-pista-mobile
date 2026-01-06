package com.example.pegapista.service

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow

object RunningState {
    val tempoSegundos = MutableStateFlow(0L)
    val distanciaMetros = MutableStateFlow(0f)
    val pace = MutableStateFlow("-:--")
    val percurso = MutableStateFlow<List<LatLng>>(emptyList())
    val isRastreando = MutableStateFlow(false)
}

