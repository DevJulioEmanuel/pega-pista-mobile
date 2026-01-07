package com.example.pegapista.ui.viewmodels
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import com.example.pegapista.data.repository.PostRepository
//
//// Esta classe serve apenas para "ensinar" o Android a criar o teu ViewModel com o Repository dentro
//class PostViewModelFactory(private val repository: PostRepository) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(PostViewModel::class.java)) {
//            @Suppress("UNCHECKED_CAST")
//            return PostViewModel(repository) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}