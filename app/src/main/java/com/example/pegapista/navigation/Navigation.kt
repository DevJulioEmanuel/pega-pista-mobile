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
import androidx.navigation.navigation
import com.example.pegapista.ui.*
import com.google.firebase.auth.FirebaseAuth


@Composable
fun NavigationGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    val auth = FirebaseAuth.getInstance()
    val usuarioAtual = auth.currentUser


    val destinoInicial = if (usuarioAtual != null) "Home" else "inicio"
    NavHost(
        navController = navController,
        startDestination = destinoInicial,
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

                    navController.navigate("Home") {
                        popUpTo("inicio") { inclusive = true }
                    }
                }
            )
        }

        composable("cadastro") {
            CadastroScreen(
                onCadastroSucesso = {

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
            AtividadeAfterScreen(
                onFinishActivity = {navController.navigate("Post")}
            )
        }
        composable ("Post"){
            RunFinishedScreen(
                onFinishNavigation =  {navController.navigate("Home")}
            )
        }

        composable("comunidade") {
            FeedScreen(
                onRankingScreen = { navController.navigate("Ranking") }
            )
        }

        composable("Ranking") {
            RankingScreen(

                onFeedScreen = { navController.popBackStack() }
            )
        }


        composable("perfil") {
            PerfilScreen()
        }

        composable("notificacoes") {
            NotificacoesScreen()
        }
    }
}