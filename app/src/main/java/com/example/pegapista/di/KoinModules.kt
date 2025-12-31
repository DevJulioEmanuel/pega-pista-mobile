package com.example.pegapista.di

import org.koin.dsl.module
import org.koin.core.module.dsl.singleOf
import org.koin.android.ext.koin.androidContext
import com.example.pegapista.database.AppDatabase
import com.example.pegapista.data.repository.CorridaRepository


val storageModule = module {
    single {
        AppDatabase.getDatabase(androidContext())
    }
    single {
        CorridaRepository(
            db = get(),
            context = androidContext()
        )
    }

}