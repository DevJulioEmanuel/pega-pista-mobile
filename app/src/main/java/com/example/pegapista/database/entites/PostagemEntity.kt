package com.example.pegapista.database.entites

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pegapista.data.models.Corrida

@Entity
data class PostagemEntity(
    @PrimaryKey
    val id: String = "",
    val userId: String = "",
    val autorNome: String = "Corredor",
    val titulo: String = "",
    val descricao: String = "",
    val corrida: Corrida = Corrida(),
    val data: Long = System.currentTimeMillis(),
    val fotoUrl: String? = null
)