package com.example.pegapista.data.models

data class Postagem(
    val id: String = "",
    val userId: String = "",
    val autorNome: String = "Corredor",
    val titulo: String = "",
    val descricao: String = "",
    val corrida: Corrida = Corrida(),
    val data: Long = System.currentTimeMillis(),
    val urlsFotos: List<String> = emptyList(),
    val curtidas: List<String> = emptyList(),
    val qtdComentarios: Int = 0
)