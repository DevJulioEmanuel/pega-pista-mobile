package com.example.pegapista.database.entites

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CorridaEntity(
    @PrimaryKey
    val id: String = "",
    val userId: String = "",
    val distanciaKm: Double = 0.0,
    val tempo: String = "",
    val pace: String = "",
    val data: Long = System.currentTimeMillis()
)