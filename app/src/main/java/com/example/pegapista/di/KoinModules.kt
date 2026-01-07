package com.example.pegapista.di

import org.koin.dsl.module
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import com.example.pegapista.database.AppDatabase
import com.example.pegapista.data.repository.CorridaRepository
import com.example.pegapista.data.repository.PostRepository
import com.example.pegapista.data.repository.UserRepository
import com.example.pegapista.ui.viewmodels.CorridaViewModel
import com.example.pegapista.ui.viewmodels.PerfilUsuarioViewModel
import com.example.pegapista.ui.viewmodels.PerfilViewModel
import com.example.pegapista.ui.viewmodels.PostViewModel

val storageModule = module {
    single { AppDatabase.getDatabase(androidContext()) }

    single {
        CorridaRepository(
            db = get(),
            context = androidContext()
        )
    }

    single {
        PostRepository(
            db = get(),
            context = androidContext()
        )
    }
    single {
        UserRepository()
    }
}

val viewModelModule = module {


    viewModel { CorridaViewModel(androidApplication()) }


    viewModel { PostViewModel(androidApplication(), get()) }

    // ViewModel do Perfil de Outros (Feed)
    viewModel { PerfilUsuarioViewModel(get()) }

    // ViewModel do Meu Perfil (Sair/Foto)
    viewModel { PerfilViewModel(get()) }
}