package com.example.pegapista.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.pegapista.data.models.Corrida

@Dao
interface CorridaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun salvarCorrida(corrida: Corrida)

    @Query("SELECT * FROM corridas WHERE sincronizado = 0")
    suspend fun getCorridasNaoSincronizadas(): List<Corrida>

    @Update
    suspend fun atualizarCorrida(corrida: Corrida)

    // Você pode adicionar métodos para listar no histórico localmente também
    @Query("SELECT * FROM corridas ORDER BY data DESC")
    suspend fun getTodasCorridas(): List<Corrida>
}