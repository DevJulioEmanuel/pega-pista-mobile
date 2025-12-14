package com.example.pegapista.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pegapista.R
import com.example.pegapista.ui.theme.PegaPistaTheme

// Modelo de dados simples para a lista
data class NotificacaoData(
    val icon: ImageVector,
    val titulo: String,
    val descricao: String,
    val tempo: String
)

@Composable
fun NotificacoesScreen(
    modifier: Modifier = Modifier
) {
    // Dados mockados conforme a imagem
    val listaNotificacoes = listOf(
        NotificacaoData(
            icon = Icons.Default.WaterDrop,
            titulo = "Não perca sua chama!",
            descricao = "Corra hoje para manter a sua sequência de 13 dias.",
            tempo = "Há 5 min"
        ),
        NotificacaoData(
            icon = Icons.Default.EmojiEvents,
            titulo = "Ranking da Semana",
            descricao = "Você subiu duas posições, veja sua classificação!",
            tempo = "Há 15 min"
        ),
        NotificacaoData(
            icon = Icons.Default.ThumbUp,
            titulo = "Like",
            descricao = "Marina Sena curtiu sua corrida",
            tempo = "Há 20 min"
        ),
        NotificacaoData(
            icon = Icons.Default.PersonAdd,
            titulo = "Solicitação de Amizade",
            descricao = "Carmen Miranda aceitou o seu pedido de amizade",
            tempo = "Há 40 min"
        ),
        NotificacaoData(
            icon = Icons.Default.Star,
            titulo = "Parabéns!",
            descricao = "Você correu 100km no total!",
            tempo = "12 de mar"
        )
    )

    // Coluna Principal
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 40.dp, bottom = 20.dp, start = 20.dp, end = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo
        Image(
            painter = painterResource(id = R.drawable.logo_aplicativo),
            contentDescription = "Logo do aplicativo",
            modifier = Modifier
                .width(180.dp)
                .height(80.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Container Principal
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Lista de Notificações
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(15.dp) // Espaço entre os itens
            ) {
                items(listaNotificacoes) { item ->
                    NotificacaoItem(data = item)
                }
            }
        }
    }
}

@Composable
fun NotificacaoItem(data: NotificacaoData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(85.dp),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícone Circular
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = data.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Textos
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = data.titulo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = data.descricao,
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    maxLines = 2
                )
            }

            // Tempo/data
            Column(
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End,
                modifier = Modifier.fillMaxHeight()
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = data.tempo,
                    fontSize = 9.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificacoesScreenPreview() {
    PegaPistaTheme {
        NotificacoesScreen()
    }
}
