package com.example.pegapista.data.models

data class Postagem(
    val id: String = "",
    val userId: String = "",
    val autorNome: String = "",
    val titulo: String = "",
    val descricao: String = "",
    val corrida: Corrida = Corrida(),
    val dataPostagem: Long = System.currentTimeMillis(),
    val likesCount: Int = 0
)