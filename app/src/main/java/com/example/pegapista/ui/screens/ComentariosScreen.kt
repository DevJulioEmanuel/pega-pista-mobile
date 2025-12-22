package com.example.pegapista.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pegapista.data.models.Comentario
import com.example.pegapista.ui.viewmodels.PostViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComentariosScreen(
    postId: String,
    onVoltar: () -> Unit,
    viewModel: PostViewModel = viewModel()
) {
    LaunchedEffect(postId) {
        viewModel.carregarComentarios(postId)
    }

    val comentarios by viewModel.comentariosState.collectAsState()
    var textoComentario by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Comentários", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(bottom = 16.dp)
        ) {

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (comentarios.isEmpty()) {
                    item {
                        Text(
                            text = "Seja o primeiro a comentar!",
                            modifier = Modifier.padding(top = 20.dp),
                            color = Color.Gray
                        )
                    }
                }

                items(comentarios) { comentario ->
                    ItemComentario(comentario)
                }
            }


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .imePadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textoComentario,
                    onValueChange = { textoComentario = it },
                    placeholder = { Text("Adicione um comentário...") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )

                IconButton(
                    onClick = {
                        if (textoComentario.isNotBlank()) {
                            viewModel.enviarComentario(postId, textoComentario)
                            textoComentario = ""
                        }
                    },
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                        .size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Enviar",
                        tint = Color.White
                    )
                }
            }
        }
    }
}


@Composable
fun ItemComentario(comentario: Comentario) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Gray),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = comentario.nomeUsuario.take(1).uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = comentario.nomeUsuario,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                val dataFormatada = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
                    .format(Date(comentario.data))
                Text(
                    text = dataFormatada,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            Text(
                text = comentario.texto,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}