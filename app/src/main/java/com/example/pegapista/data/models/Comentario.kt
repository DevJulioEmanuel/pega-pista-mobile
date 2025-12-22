package com.example.pegapista.data.models

data class Comentario(
    val id: String = "",
    val userId: String = "",
    val nomeUsuario: String = "",
    val texto: String = "",
    val data: Long = System.currentTimeMillis()
)
