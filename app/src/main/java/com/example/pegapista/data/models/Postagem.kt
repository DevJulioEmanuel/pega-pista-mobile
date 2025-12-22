package com.example.pegapista.data.models

data class Postagem(
    val id: String = "",
    val userId: String = "",
    val autorNome: String = "Corredor",
    val titulo: String = "",
    val descricao: String = "",
    val corrida: Corrida = Corrida(),
    val data: Long = System.currentTimeMillis(),
    val fotoUrl: String? = null
)