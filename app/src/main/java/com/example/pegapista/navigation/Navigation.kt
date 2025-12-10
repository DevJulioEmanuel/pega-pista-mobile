package com.example.pegapista.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.pegapista.ui.screens.AtividadeAfterScreen
import com.example.pegapista.ui.screens.AtividadeBeforeScreen
import com.example.pegapista.ui.screens.CadastroScreen
import com.example.pegapista.ui.screens.FeedScreen
import com.example.pegapista.ui.screens.HomeScreen
import com.example.pegapista.ui.screens.InicioScreen
import com.example.pegapista.ui.screens.LoginScreen
import com.example.pegapista.ui.screens.NotificacoesScreen
import com.example.pegapista.ui.screens.PerfilScreen


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
            CadastroScreen(
                onCadastroSucesso = {
                    // Quando o cadastro termina, vai para a Home.
                    // O popUpTo limpa a pilha para que o botão "Voltar" não retorne ao cadastro.
                    navController.navigate("Home") {
                        popUpTo("inicio") { inclusive = true }
                    }
                }
            )
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