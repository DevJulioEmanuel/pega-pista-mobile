package com.example.pegapista.di

import androidx.room.Room
import com.example.pegapista.data.repository.CorridaRepository
import kotlin.jvm.java

val storageModule = module {
    singleOf(::CorridaRepository)
    single{
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database-name"
        ).build()
    }
}