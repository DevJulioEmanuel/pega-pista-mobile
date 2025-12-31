package com.example.pegapista.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pegapista.database.entites.CorridaEntity

@Dao
interface CorridaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(corrida: CorridaEntity)
}