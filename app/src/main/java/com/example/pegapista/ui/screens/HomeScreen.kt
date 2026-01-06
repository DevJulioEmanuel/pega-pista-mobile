package com.example.pegapista.ui.screens

import com.example.pegapista.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.Typeface
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.pegapista.ui.theme.BackgroundLight
import com.example.pegapista.ui.theme.BluePrimary
import com.example.pegapista.ui.theme.PegaPistaTheme
import kotlin.math.sin
import com.example.pegapista.data.models.Usuario
import com.example.pegapista.data.models.Postagem

@Composable
fun HomeScreen(
    usuario: Usuario?,
    ranking: List<Usuario> = emptyList(),
    atividades: List<Postagem> = emptyList(),
    onIniciarCorrida: () -> Unit
) {
    val dias = usuario?.diasSeguidos ?: 0
    val meuId = usuario?.id ?: ""
    val minhaPosicao = ranking.indexOfFirst { it.id == meuId }.let { if (it == -1) "--" else "${it + 1}º" }
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//        Image(
//            painter = painterResource(R.drawable.logo_aplicativo),
//            contentDescription = "Logo do aplicativo",
//            modifier = Modifier.size(80.dp)
//        )
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.5f)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    // Lado Esquerdo (Fogo)
                    Image(
                        painter = painterResource(R.drawable.logo_fogo),
                        contentDescription = "Fogo de sequência",
                        modifier = Modifier.size(90.dp)
                    )

                    // Lado Direito (Info)

                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.padding(end = 2.dp)
                    ) {

                        // 1. O Balão Branco (Cima)
                        Surface(
                            shape = RoundedCornerShape(30),
                            color = Color.White,
                            modifier = Modifier.offset(y = 10.dp).zIndex(1f) //deixar um por cima do oto
                        ) {
                            Text(
                                text = "Sequência de $dias dias consecutivos",
                                color = Color.Black,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 15.dp, vertical = 6.dp)
                            )
                        }

                        // 2. O Quadrado Azul Escuro (Baixo)
                        Surface(
                            shape = RoundedCornerShape(20),
                            color = Color(0xFF0277BD),
                            modifier = Modifier.padding(top = 0.dp, end = 2.dp)
                        ) {
                            Text(
                                text = "Faltam 3 dias para bater\nsua meta!",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 40.dp, vertical = 7.dp)
                            )
                        }
                    }

                }
                // ESPAÇAMENTO ENTRE TOPICOS
                Spacer(modifier = Modifier.height(35.dp))

// --- LINHA DO MEIO (SEU RANK vs AMIGOS) ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // --- LADO ESQUERDO:
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = minhaPosicao,
                            fontSize = 70.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )


                        Surface(
                            shape = RoundedCornerShape(30),
                            color = Color.White
                        ) {
                            Text(
                                text = "Está é sua posição no ranking dos amigos",
                                fontSize = 7.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1, // <--- Força a ser apenas 1 linha
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .padding(horizontal = 8.dp, vertical = 1.dp)
                            )
                        }
                    }

                    // --- LADO DIREITO: TÍTULO DO RANKING ---

                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 16.dp)
                    ) {

                        // --- LADO DIREITO: RANKING ---

                        Text(
                            text = "Ranking dos Amigos 🏆",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 8.dp, end = 8.dp)
                        )

                        ranking.take(2).forEachIndexed { index, user ->
                            ItemRankingHome(
                                posicao = "${index + 1}º",
                                nome = user.nickname,
                                largura = if (index == 0) 1f else 0.8f
                            )

                        }
                    }
                }
                // --- ESPAÇO DEPOIS DO RANKING ---
                Spacer(modifier = Modifier.height(35.dp))


                Text(
                    text = "Últimas Atividades dos Amigos",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth() // Para conseguir centralizar o texto
                        .padding(bottom = 8.dp),
                    textAlign = TextAlign.Center
                )

                if (atividades.isEmpty()) {
                    Text(
                        text = "Nenhuma atividade recente dos amigos.",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                } else {
                    atividades.forEach { post ->
                        ItemAtividade(
                            nome = post.autorNome,
                            info = "${post.corrida.distanciaKm} km em ${post.corrida.tempo} min"
                        )
                    }
                }

        // Este Spacer é mágico: ele ocupa TODO o espaço vazio que sobrar, empurrando o botão lá para baixo
                Spacer(modifier = Modifier.weight(1f))

                Spacer(modifier = Modifier.height(22.dp))
// --- BOTÃO INICIAR CORRIDA ---
                Button(
                    onClick = onIniciarCorrida,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF017BB6)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = "Iniciar Corrida",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                }

                }
            }

        }


@Composable
fun ItemAtividade(nome: String, info: String) {
    Surface(
        color = Color(0xFF0277BD),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            // Ícone do Usuário
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            //
            Column {
                Text(
                    text = nome,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Text(
                    text = info,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 10.sp
                )
            }
        }
    }
}
@Composable
fun ItemRankingHome(posicao: String, nome: String, largura: Float) {
    Surface(
        
        color = Color(0xFF0288D1),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth(largura) // <--- O SEGREDO: A largura varia (0.1 a 1.0)
            .padding(vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = posicao,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )

            Spacer(modifier = Modifier.width(6.dp))


            Icon(
                imageVector = Icons.Default.AccountCircle, // Precisa importar icons
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            // Nome da Pessoa
            Text(
                text = nome,
                color = Color.White,
                fontSize = 10.sp,
                maxLines = 1 // Garante que não quebra linha
            )
        }
    }
}
@Preview (showBackground = true)
@Composable
fun HomeScreenPreview() {
    PegaPistaTheme {
        HomeScreen(usuario = null, onIniciarCorrida = {})
    }
}