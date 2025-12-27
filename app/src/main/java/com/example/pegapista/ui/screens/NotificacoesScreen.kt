package com.example.pegapista.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ModeComment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pegapista.data.models.Notificacao
import com.example.pegapista.data.models.TipoNotificacao
import com.example.pegapista.ui.viewmodels.NotificationsViewModel
import java.util.concurrent.TimeUnit

@Composable
fun NotificacoesScreen(
    modifier: Modifier = Modifier,
    viewModel: NotificationsViewModel = viewModel()
) {
    val listaNotificacoes by viewModel.notificacoes.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.carregarNotificacoes()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Notificações",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 15.dp).align(Alignment.Start)
        )

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

            if (listaNotificacoes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Nenhuma notificação ainda.",
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            } else {
                // Lista Real
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    items(listaNotificacoes) { item ->
                        NotificacaoItem(notificacao = item)
                    }
                }
            }
        }
    }
}

@Composable
fun NotificacaoItem(notificacao: Notificacao) {
    val (icon, tituloPadrao) = when (notificacao.tipo) {
        TipoNotificacao.SEGUIR -> Pair(Icons.Default.PersonAdd, "Novo Seguidor")
        TipoNotificacao.CURTIDA -> Pair(Icons.Default.ThumbUp, "Curtida")
        TipoNotificacao.COMENTARIO -> Pair(Icons.Default.ModeComment, "Comentário")
        else -> Pair(Icons.Default.Notifications, "Aviso")
    }

    val tempoRelativo = calcularTempoRelativo(notificacao.data)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(85.dp),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notificacao.lida) MaterialTheme.colorScheme.surface else Color(0xFFEDF7FF) // Leve destaque se não lida
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = tituloPadrao,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = notificacao.mensagem,
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    maxLines = 2
                )
            }

            Column(
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End,
                modifier = Modifier.fillMaxHeight()
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = tempoRelativo,
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

fun calcularTempoRelativo(timestamp: Long): String {
    val agora = System.currentTimeMillis()
    val diff = agora - timestamp

    return when {
        diff < TimeUnit.MINUTES.toMillis(1) -> "Agora"
        diff < TimeUnit.HOURS.toMillis(1) -> "Há ${TimeUnit.MILLISECONDS.toMinutes(diff)} min"
        diff < TimeUnit.DAYS.toMillis(1) -> "Há ${TimeUnit.MILLISECONDS.toHours(diff)} h"
        diff < TimeUnit.DAYS.toMillis(7) -> "Há ${TimeUnit.MILLISECONDS.toDays(diff)} dias"
        else -> {
            val sdf = java.text.SimpleDateFormat("dd/MM", java.util.Locale.getDefault())
            sdf.format(java.util.Date(timestamp))
        }
    }
}