package com.example.pegapista.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.pegapista.ui.screens.AtividadeAfterScreen
import com.example.pegapista.ui.screens.AtividadeBeforeScreen
import com.example.pegapista.ui.screens.BuscarAmigosScreen
import com.example.pegapista.ui.screens.CadastroScreen
import com.example.pegapista.ui.screens.ComentariosScreen
import com.example.pegapista.ui.screens.FeedScreen
import com.example.pegapista.ui.screens.HomeScreen
import com.example.pegapista.ui.screens.InicioScreen
import com.example.pegapista.ui.screens.LoginScreen
import com.example.pegapista.ui.screens.NotificacoesScreen
import com.example.pegapista.ui.screens.PerfilScreen
import com.example.pegapista.ui.screens.PerfilUsuarioScreen
import com.example.pegapista.ui.screens.RankingScreen
import com.example.pegapista.ui.screens.RunFinishedScreen
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth



@RequiresApi(Build.VERSION_CODES.O)
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


        composable("Home") {
            HomeScreen(
                usuario = usuarioAtual,
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
                onFinishActivity = { dist, tempo, pace, listaPontos ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("rota_gps", listaPontos)
                    navController.navigate("RunFinished/$dist/$tempo/$pace")
                },
                onCancelActivity = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "RunFinished/{distancia}/{tempo}/{pace}",
            arguments = listOf(
                navArgument("distancia") { type = NavType.FloatType },
                navArgument("tempo") { type = NavType.StringType },
                navArgument("pace") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val listaPontos = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<List<LatLng>>("rota_gps") ?: emptyList()

            val distancia = backStackEntry.arguments?.getFloat("distancia")?.toDouble() ?: 0.0
            val tempo = backStackEntry.arguments?.getString("tempo") ?: "00:00"
            val pace = backStackEntry.arguments?.getString("pace") ?: "-:--"

            RunFinishedScreen(
                distancia = distancia,
                tempo = tempo,
                pace = pace,
                caminhoPercorrido = listaPontos,
                onFinishNavigation = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        composable("comunidade") {
            FeedScreen(
                onRankingScreen = {
                    navController.navigate("Ranking")
                },
                onBuscarAmigosScreen = {
                    navController.navigate("BuscarAmigos")
                },
                onCommentClick = { post ->
                    navController.navigate("comentarios/${post.id}/${post.userId}")
                }
            )
        }

        composable(
            route = "comentarios/{postId}/{remetenteId}",
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: ""
            val remetenteId = backStackEntry.arguments?.getString("remetenteId") ?: ""
            ComentariosScreen(
                postId = postId,
                remetenteId = remetenteId,
                onVoltar = { navController.popBackStack() }
            )
        }

        composable("Ranking") {
            RankingScreen(
                onFeedScreen = { navController.popBackStack() },
                onBuscarAmigosScreen = {
                    navController.navigate("BuscarAmigos")
                }
            )
        }

        composable("BuscarAmigos") {
            BuscarAmigosScreen(
                onPerfilUsuarioScreen = { idUsuario ->
                    navController.navigate("PerfilUsuario/$idUsuario")
                }
            )
        }

        composable(route="PerfilUsuario/{idUsuario}") {  backStackEntry ->
            val idUsuario = backStackEntry.arguments?.getString("idUsuario") ?: ""
            PerfilUsuarioScreen(
                idUsuario = idUsuario,
                onCommentClick = { post ->
                    navController.navigate("comentarios/${post.id}")
                })
        }


        composable("perfil") {
            PerfilScreen()
        }

        composable("notificacoes") {
            NotificacoesScreen()
        }
    }
}