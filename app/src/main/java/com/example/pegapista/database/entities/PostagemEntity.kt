package com.example.pegapista.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "postagem")
data class PostagemEntity(
    @PrimaryKey val id: String = "",
    val userId: String = "",
    val autorNome: String = "Corredor",
    val titulo: String = "",
    val descricao: String = "",

    val distanciaKm: Double = 0.0,
    val tempo: String = "00:00",
    val pace: String = "-:--",

    val data: Long = System.currentTimeMillis(),
    val fotoUrl: String? = null,
    val postsincronizado: Boolean = false
)