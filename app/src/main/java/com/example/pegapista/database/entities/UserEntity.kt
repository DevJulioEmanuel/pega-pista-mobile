package com.example.pegapista.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserEntity (
    @PrimaryKey
    val id: String = "",
    val nickname: String = "",
    val email: String = "",
    val fotoPerfilUrl: String? = null,
    val distanciaTotalKm: Double = 0.0,
    val tempoTotalSegundos: Long = 0,
    val caloriasQueimadas: Int = 0,
    val diasSeguidos: Int = 0,
    val recordeDiasSeguidos: Int = 0
)