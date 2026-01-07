package com.example.pegapista.utils

import com.example.pegapista.data.models.Corrida
import com.example.pegapista.data.models.Postagem
import com.example.pegapista.database.entities.PostagemEntity

// Função que converte a Entidade do Banco (PostagemEntity) para o Modelo do App (Postagem)
fun PostagemEntity.toModel(): Postagem {
    return Postagem(
        id = this.id,
        userId = this.userId,
        autorNome = this.autorNome,
        titulo = this.titulo,
        descricao = this.descricao,

        corrida = Corrida(
            distanciaKm = this.distanciaKm,
            tempo = this.tempo,
            pace = this.pace
        ),

        data = this.data,

        urlsFotos = if (this.fotoUrl.isNullOrBlank()) {
            emptyList()
        } else {
            this.fotoUrl.split(";")
        }
    )
}