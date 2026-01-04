package com.example.pegapista.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pegapista.data.models.Usuario
import com.example.pegapista.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RankingViewModel : ViewModel() {
    private val repository = UserRepository()
    private val _ranking = MutableStateFlow<List<Usuario>>(emptyList())
    val ranking: StateFlow<List<Usuario>> = _ranking

    init {
        carregarRanking()
    }

    fun carregarRanking() {
        viewModelScope.launch {
            _ranking.value = repository.getRankingSeguindo()
        }
    }

}
