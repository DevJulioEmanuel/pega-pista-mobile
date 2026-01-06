package com.example.pegapista

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pegapista.navigation.NavigationGraph
import com.example.pegapista.ui.screens.BottomBar

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PegaPistaScreen() {

    val navController = rememberNavController()


    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val screensWithBottomBar = listOf(
        "Home",
        "comunidade",
        "Ranking",
        "perfil",
        "AtividadeBefore",
        "AtividadeAfter",
        "notificacoes",
        "BuscarAmigos",
        "PerfilUsuario/{idUsuario}"
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