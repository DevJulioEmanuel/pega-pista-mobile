package com.example.pegapista.data.models

import com.google.firebase.Timestamp
import java.util.Date

data class Corrida(
    val id: String = "",
    val userId: String = "",
    val distanciaKm: Double = 0.0,
    val tempo: String = "",
    val pace: String = "",
    val data: Long = System.currentTimeMillis()
)