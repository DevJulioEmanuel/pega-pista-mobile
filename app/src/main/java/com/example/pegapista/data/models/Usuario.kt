package com.example.pegapista.data.models

data class Usuario(
    val id: String = "",
    val nickname: String = "",
    val email: String = "",
    val fotoPerfilUrl: String? = null,
    val distanciaTotalKm: Double = 0.0,
    val tempoTotalSegundos: Long = 0,
    val caloriasQueimadas: Int = 0,
    val diasSeguidos: Int = 0,
    val recordeDiasSeguidos: Int = 0,
    val ultimaAtividade: Long = 0
)