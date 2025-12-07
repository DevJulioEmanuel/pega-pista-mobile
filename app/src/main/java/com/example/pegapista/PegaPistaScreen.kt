package com.example.pegapista

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pegapista.navigation.NavigationGraph
import com.example.pegapista.ui.BottomBar

@Composable
fun PegaPistaScreen() {
    // 1. O Controlador de navegação é criado AQUI
    val navController = rememberNavController()

    // 2. Observamos a rota atual para decidir se mostramos a barra ou não
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 3. Lista de telas que DEVEM ter a barra inferior
    val screensWithBottomBar = listOf(
        "Home",
        "comunidade",
        "ranking",
        "perfil",
        "AtividadeBefore",
        "AtividadeAfter",
        "notificacoes"
    )


    val showBottomBar = currentRoute in screensWithBottomBar

    Scaffold(
        bottomBar = {

            if (showBottomBar) {
                BottomBar(
                    currentRoute = currentRoute,
                    onItemClick = { route ->
                        navController.navigate(route) {
                            // Configuração padrão para evitar empilhar telas iguais
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->

        NavigationGraph(
            navController = navController,
            modifier = Modifier.padding(paddingValues)
        )
    }
}