package com.example.pegapista.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.pegapista.service.RunningState
import com.example.pegapista.ui.screens.*
import com.example.pegapista.ui.viewmodels.HomeViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val auth = FirebaseAuth.getInstance()
    val usuarioAtual = auth.currentUser
    val isCorrendo = RunningState.isRastreando.collectAsState().value || RunningState.tempoSegundos.collectAsState().value > 0f

    val destinoInicial = if (usuarioAtual != null) {
        if (isCorrendo) "AtividadeAfter" else "main"
    } else {
        "inicio"
    }

    NavHost(
        navController = navController,
        startDestination = destinoInicial,
        modifier = modifier
    ) {
        // --- TELA PRINCIPAL (COM SWIPE E BARRA) ---
        composable("main") {
            MainContainer(
                navController = navController,
                onDeslogar = {
                    navController.navigate("inicio") {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // --- TELAS FORA DO CONTAINER (SEM BARRA EMBAIXO) ---

        composable("inicio") {
            Box(Modifier.fillMaxSize().background(Color.White).systemBarsPadding()) {
                InicioScreen(
                    onEntrarClick = { navController.navigate("login") },
                    onCadastrarClick = { navController.navigate("cadastro") }
                )
            }
        }

        composable("login") {
            LoginScreen(
                onVoltarClick = { navController.popBackStack() },
                onEntrarHome = {
                    navController.navigate("main") { popUpTo("inicio") { inclusive = true } }
                }
            )
        }

        composable("cadastro") {
            CadastroScreen(
                onCadastroSucesso = {
                    navController.navigate("main") { popUpTo("inicio") { inclusive = true } }
                }
            )
        }

        composable("AtividadeAfter") {
            Box(Modifier.fillMaxSize().background(Color.White).systemBarsPadding()) {
                AtividadeAfterScreen(
                    onFinishActivity = { dist, tempo, pace, listaPontos ->
                        navController.currentBackStackEntry?.savedStateHandle?.set("rota_gps", listaPontos)
                        navController.navigate("RunFinished/$dist/$tempo/$pace")
                    },
                    onCancelActivity = {
                        navController.navigate("main") { popUpTo("main") { inclusive = true } }
                    }
                )
            }
        }

        composable("Mapa") {
            Box(Modifier.fillMaxSize().background(Color.White).statusBarsPadding()) {
                MapaVisualizacaoScreen(onVoltar = { navController.popBackStack() })
            }
        }

        composable(
            route = "RunFinished/{distancia}/{tempo}/{pace}",
            arguments = listOf(
                navArgument("distancia") { type = NavType.FloatType },
                navArgument("tempo") { type = NavType.StringType },
                navArgument("pace") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val listaPontos = navController.previousBackStackEntry?.savedStateHandle?.get<List<LatLng>>("rota_gps") ?: emptyList()
            val distancia = backStackEntry.arguments?.getFloat("distancia")?.toDouble() ?: 0.0
            val tempo = backStackEntry.arguments?.getString("tempo") ?: "00:00"
            val pace = backStackEntry.arguments?.getString("pace") ?: "-:--"

            Box(Modifier.fillMaxSize().background(Color.White).systemBarsPadding()) {
                RunFinishedScreen(
                    distancia = distancia,
                    tempo = tempo,
                    pace = pace,
                    caminhoPercorrido = listaPontos,
                    onFinishNavigation = {
                        navController.navigate("main") { popUpTo("main") { inclusive = true } }
                    }
                )
            }
        }

        composable("Ranking") {
            Box(Modifier.fillMaxSize().background(Color.White).statusBarsPadding()) {
                RankingScreen(
                    onFeedScreen = { navController.popBackStack() },
                    onBuscarAmigosScreen = { navController.navigate("BuscarAmigos") }
                )
            }
        }

        composable("BuscarAmigos") {
            // CORREÇÃO: Padding no topo
            Box(Modifier.fillMaxSize().background(Color.White).statusBarsPadding()) {
                BuscarAmigosScreen(
                    onPerfilUsuarioScreen = { idUsuario -> navController.navigate("PerfilUsuario/$idUsuario") }
                )
            }
        }

        composable("PerfilUsuario/{idUsuario}") { backStackEntry ->
            val idUsuario = backStackEntry.arguments?.getString("idUsuario") ?: ""
            // CORREÇÃO: Padding no topo
            Box(Modifier.fillMaxSize().background(Color.White).statusBarsPadding()) {
                PerfilUsuarioScreen(
                    idUsuario = idUsuario,
                    onCommentClick = { post -> navController.navigate("comentarios/${post.id}/${idUsuario}") }
                )
            }
        }

        composable(
            route = "comentarios/{postId}/{remetenteId}",
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: ""
            val remetenteId = backStackEntry.arguments?.getString("remetenteId") ?: ""
            // Comentários já tem tratamento interno no Scaffold dele, mas por segurança:
            ComentariosScreen(
                postId = postId,
                remetenteId = remetenteId,
                onVoltar = { navController.popBackStack() }
            )
        }

        composable("notificacoes") {
            // Se essa tela for usada fora do MainContainer, precisa de padding
            Box(Modifier.fillMaxSize().background(Color.White).statusBarsPadding()) {
                NotificacoesScreen()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainContainer(
    navController: NavHostController,
    onDeslogar: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 5 })
    val scope = rememberCoroutineScope()

    // Sincroniza qual aba está ativa na BottomBar
    val currentRouteStub = when (pagerState.currentPage) {
        0 -> "Home"
        1 -> "comunidade"
        2 -> "perfil"
        3 -> "AtividadeBefore"
        4 -> "notificacoes"
        else -> "Home"
    }

    Scaffold(
        bottomBar = {
            BottomBar(
                currentRoute = currentRouteStub,
                onItemClick = { route ->
                    val page = when (route) {
                        "Home" -> 0
                        "comunidade" -> 1
                        "perfil" -> 2
                        "AtividadeBefore" -> 3 // Garanta que o nome da rota bata com sua BottomBar
                        "notificacoes" -> 4
                        else -> 0
                    }
                    scope.launch { pagerState.animateScrollToPage(page) }
                }
            )
        }
    ) { paddingValues ->
        // O paddingValues do Scaffold cuida da BottomBar e da StatusBar AQUI DENTRO
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { page ->
            when (page) {
                0 -> { // HOME
                    val homeViewModel: HomeViewModel = viewModel()
                    val usuario by homeViewModel.usuario.collectAsState()
                    val ranking by homeViewModel.ranking.collectAsState()
                    HomeScreen(
                        usuario = usuario,
                        ranking = ranking,
                        onIniciarCorrida = { navController.navigate("AtividadeBefore") }
                    )
                }
                1 -> { // COMUNIDADE
                    FeedScreen(
                        onRankingScreen = { navController.navigate("Ranking") },
                        onBuscarAmigosScreen = { navController.navigate("BuscarAmigos") },
                        onCommentClick = { post -> navController.navigate("comentarios/${post.id}/${post.userId}") },
                        onProfileClick = { userId -> navController.navigate("PerfilUsuario/$userId") }
                    )
                }
                2 -> { // PERFIL
                    PerfilScreen(
                        onDeslogar = onDeslogar,
                        onCommentClick = { post, userId ->
                            navController.navigate("comentarios/${post.id}/${userId}") // Ajustei aqui, antes estava post.userId que as vezes é null no card de perfil
                        }
                    )
                }
                3 -> { // ATIVIDADE (Aba)
                    AtividadeBeforeScreen(
                        onStartActivity = { navController.navigate("AtividadeAfter") },
                        onAbrirMapa = { navController.navigate("Mapa") }
                    )
                }
                4 -> { // NOTIFICACOES
                    NotificacoesScreen()
                }
            }
        }
    }
}