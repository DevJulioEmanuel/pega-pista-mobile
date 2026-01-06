package com.example.pegapista.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.pegapista.data.models.Corrida
import com.example.pegapista.database.entities.CorridaEntity
@Dao
interface CorridaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun salvarCorrida(corrida: CorridaEntity)

    @Query("SELECT * FROM corridas WHERE sincronizado = 0")
    suspend fun getCorridasNaoSincronizadas(): List<CorridaEntity>

    @Update
    suspend fun atualizarCorrida(corrida: CorridaEntity)

    @Query("SELECT * FROM corridas ORDER BY data DESC")
    suspend fun getTodasCorridas(): List<CorridaEntity>
}

fun CorridaEntity.toModel(): Corrida {
    return Corrida(
        id = this.id,
        distanciaKm = this.distanciaKm,
        tempo = this.tempo,
        pace = this.pace
    )
}

fun Corrida.toEntity(): CorridaEntity {
    return CorridaEntity(
        id = this.id.toString(),
        distanciaKm = this.distanciaKm,
        tempo = this.tempo,
        pace = this.pace,
        sincronizado = false
    )
}