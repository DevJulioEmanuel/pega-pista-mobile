package com.example.pegapista.ui.screens

import com.example.pegapista.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pegapista.ui.theme.PegaPistaTheme

@Composable
fun InicioScreen(
    // 1. CORREÇÃO: Os parâmetros ficam AQUI (dentro dos parênteses), não lá em baixo
    onEntrarClick: () -> Unit,
    onCadastrarClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Image(
            painter = painterResource(R.drawable.logo_aplicativo),
            contentDescription = "Logo do aplicativo",
            modifier = Modifier
                .size(400.dp)
                .padding(top = 0.dp),
        )

        Spacer(modifier = Modifier.weight(1f))

        Card(
            shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = "Seja bem-vindo(a)!",
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 20.dp, start = 20.dp, bottom = 5.dp)
                )
                Text(
                    text = "Comece a carregar atividades, compita com amigos e, acima de tudo, divirta-se!",
                    color = Color.White.copy(alpha = 0.8f),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(start = 20.dp, bottom = 10.dp)
                )

                // --- BOTÃO ENTRAR ---
                Button(
                    // 2. AQUI: Quando clicar, aciona o "onEntrarClick"
                    onClick = onEntrarClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF068EC9)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(top = 10.dp)
                ) {
                    Text(
                        text = "Entrar",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // --- BOTÃO CADASTRAR ---
                Button(
                    onClick = onCadastrarClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF068EC9)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(top = 10.dp)
                ) {
                    Text(
                        text = "Cadastre-se",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Preview (showBackground = true)
@Composable
fun InicioScreenPreview() {
    PegaPistaTheme {
        // Passamos funções vazias {} só para o preview funcionar
        InicioScreen(onEntrarClick = {}, onCadastrarClick = {})
    }
}