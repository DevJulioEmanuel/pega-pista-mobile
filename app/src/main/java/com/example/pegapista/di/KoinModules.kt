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
import com.example.pegapista.ui.viewmodels.PostViewModel

val storageModule = module {
    // 1. Banco de Dados
    single { AppDatabase.getDatabase(androidContext()) }

    // 2. Repositório de Corridas (Usa o DB e o Contexto)
    single {
        CorridaRepository(
            db = get(),
            context = androidContext()
        )
    }

    // 3. Repositório de Postagens (Usa o DB e o Contexto)
    single {
        PostRepository(
            db = get(),
            context = androidContext()
        )
    }

}
val viewModelModule = module {

    // ViewModel da Corrida
    // (Usa androidApplication() porque herda de AndroidViewModel)
    viewModel { CorridaViewModel(androidApplication()) }

    // --- CORREÇÃO AQUI ---
    // Removemos o "repository ="
    // O Koin vai injetar o PostRepository automaticamente pelo tipo.
    viewModel { PostViewModel(get()) }

    // --- CORREÇÃO AQUI ---
    // Removemos o "repository ="
    // Provavelmente no seu ViewModel o nome é 'postRepository',
    // mas usando apenas get() não precisamos nos preocupar com o nome.
    viewModel { PerfilUsuarioViewModel(get()) }
}
