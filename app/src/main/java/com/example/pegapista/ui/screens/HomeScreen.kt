package com.example.pegapista.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.pegapista.R
import com.example.pegapista.data.models.Postagem
import com.example.pegapista.data.models.Usuario

@Composable
fun HomeScreen(
    usuario: Usuario?,
    ranking: List<Usuario> = emptyList(),
    atividades: List<Postagem> = emptyList(),
    onIniciarCorrida: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- 1. SEÇÃO DO FOGUINHO (SEQUÊNCIA ATUAL) ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_fogo),
                    contentDescription = "Foguinho",
                    modifier = Modifier.size(60.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Sua Sequência",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                    Text(
                        text = "${usuario?.diasSeguidos ?: 0} Dias Seguidos!",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- 2. RANKING DOS AMIGOS (CARROSSEL COM FOTOS) ---
        Text(
            text = "Ranking dos Amigos 🏆",
            modifier = Modifier.fillMaxWidth(),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (ranking.isEmpty()) {
            Text(
                "Siga amigos para ver o ranking",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(vertical = 10.dp)
            )
        } else {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(ranking) { index, amigo ->
                    CardRankingAmigo(amigo, posicao = index + 1)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- 3. ÚLTIMAS ATIVIDADES ---
        Text(
            text = "Últimas Atividades",
            modifier = Modifier.fillMaxWidth(),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (atividades.isEmpty()) {
            Text("Nenhuma atividade recente.", color = Color.Gray, fontSize = 14.sp)
        } else {
            atividades.forEach { post ->
                ItemAtividadeHome(post)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- 4. BOTÃO INICIAR CORRIDA ---
        Button(
            onClick = onIniciarCorrida,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("INICIAR CORRIDA", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun CardRankingAmigo(amigo: Usuario, posicao: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(90.dp)
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            // Foto do Usuário
            val imageModifier = Modifier
                .size(75.dp)
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)

            if (!amigo.fotoPerfilUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = amigo.fotoPerfilUrl,
                    contentDescription = null,
                    modifier = imageModifier,
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.jaco),
                    contentDescription = null,
                    modifier = imageModifier,
                    contentScale = ContentScale.Crop
                )
            }

            // Badge de Posição
            Surface(
                color = MaterialTheme.colorScheme.secondary,
                shape = CircleShape,
                modifier = Modifier
                    .size(26.dp)
                    .offset(x = 4.dp, y = (-4).dp)
            ) {
                Text(
                    text = "$posicao",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.wrapContentHeight()
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = amigo.nickname,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${amigo.diasSeguidos}",
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.width(2.dp))
            Image(
                painter = painterResource(R.drawable.logo_fogo),
                contentDescription = null,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
fun ItemAtividadeHome(post: Postagem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = post.autorNome, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(
                    text = "${post.corrida.distanciaKm}km em ${post.corrida.tempo}",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}
