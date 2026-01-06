package com.example.pegapista

import android.app.Application
import com.example.pegapista.di.storageModule // Seu módulo
import com.example.pegapista.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PegaPistaApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@PegaPistaApplication)
            modules(listOf(storageModule, viewModelModule))
        }
    }
}