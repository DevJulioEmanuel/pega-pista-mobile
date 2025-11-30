package com.example.pegapista

import androidx.compose.material3.Text
import androidx.compose.runtime.* // Importante para o remember, mutableStateOf
import com.example.pegapista.ui.InicialScreen
import com.example.pegapista.ui.InicioScreen
import com.example.pegapista.ui.LoginScreen
import kotlinx.coroutines.delay // Importante para o tempo (delay)

@Composable
fun PegaPistaApp() {
    // Controla qual tela aparece
    var telaAtual by remember { mutableStateOf("Splash") }

    // Tempo da Splash
    LaunchedEffect(Unit) {
        if (telaAtual == "Splash") {
            delay(3000)
            telaAtual = "Inicio"
        }
    }

    // Decide qual tela desenhar
    when (telaAtual) {
        "Splash" -> {
            InicialScreen()
        }
        "Inicio" -> {
            // AQUI É A LIGAÇÃO MÁGICA
            InicioScreen(
                onEntrarClick = {
                    telaAtual = "Login" // <-- ISSO FAZ MUDAR PARA A TELA DE LOGIN
                },
                onCadastrarClick = {
                    telaAtual = "Cadastro"
                }
            )
        }
        "Login" -> {
            // Aqui ele desenha a tua tela de Login
            LoginScreen()
        }
        "Cadastro" -> {
            // CadastroScreen()
            Text("Tela de Cadastro")
        }
    }
}