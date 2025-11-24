package com.example.pegapista.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun PegaPistaScreen() {

    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomBar(
                currentRoute = navController.currentDestination?.route,
                onItemClick = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                        popUpTo(navController.graph.startDestinationId)
                    }
                }
            )
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "comunidade",
            modifier = Modifier.padding(padding)
        ) {

            composable("inicio") { LoginScreen() }
            composable("comunidade") { FeedScreen() }
            composable("perfil") { PerfilScreen() }
            composable("atividade") {  }
            composable("notificacoes") {}

        }
    }
}
