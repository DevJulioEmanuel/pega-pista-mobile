package com.example.pegapista.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "corridas")data class CorridaEntity(
    @PrimaryKey
    val id: String = "",
    val userId: String = "",
    val distanciaKm: Double = 0.0,
    val tempo: String = "",
    val pace: String = "",
    val data: Long = System.currentTimeMillis(),
    val sincronizado: Boolean = false
)