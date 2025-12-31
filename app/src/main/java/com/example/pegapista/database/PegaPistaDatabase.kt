package com.example.pegapista.database

import androidx.room.Database
import com.example.pegapista.data.models.Corrida
import com.example.pegapista.database.dao.CorridaDao
import com.example.pegapista.database.entites.CorridaEntity
import com.example.pegapista.database.entites.PostagemEntity
import com.example.pegapista.database.entites.UserEntity

@Database(entities = [CorridaEntity::class, UserEntity::class, PostagemEntity::class], version = 1)
abstract class PegaPistaDatabase{

    abstract fun corridaDao(): CorridaDao

}