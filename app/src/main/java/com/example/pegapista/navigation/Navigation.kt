package com.example.pegapista.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.pegapista.ui.*



@Composable
fun NavigationGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "inicio",
        modifier = modifier
    ) {
        // --- Telas SEM Barra (não estão na lista do PegaPistaScreen) ---
        composable("inicio") {
            InicioScreen(
                onEntrarClick = { navController.navigate("login") },
                onCadastrarClick = { navController.navigate("cadastro") }
            )
        }

        composable("login") {
            LoginScreen(
                onVoltarClick = { navController.popBackStack() },
                onEntrarHome = {
                    // Remove o login da pilha para não voltar para ele
                    navController.navigate("Home") {
                        popUpTo("inicio") { inclusive = true }
                    }
                }
            )
        }

        composable("cadastro") {
            CadastroScreen()
        }

        // --- Telas COM Barra (estão na lista do PegaPistaScreen) ---

        composable("Home") {
            HomeScreen(
                onIniciarCorrida = { navController.navigate("AtividadeBefore") }
            )
        }

        composable("AtividadeBefore") {
            AtividadeBeforeScreen(
                onStartActivity = { navController.navigate("AtividadeAfter") }
            )
        }

        composable("AtividadeAfter") {
            AtividadeAfterScreen()
        }

        composable("comunidade") {
            FeedScreen()
        }

        composable("ranking") {
            // RankingScreen()
        }

        composable("perfil") {
            PerfilScreen()
        }

        composable("notificacoes") {
            NotificacoesScreen()
        }
    }
}