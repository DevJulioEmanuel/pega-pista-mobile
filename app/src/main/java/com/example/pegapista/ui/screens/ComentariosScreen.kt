package com.example.pegapista.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.pegapista.R
import com.example.pegapista.data.models.Comentario
import com.example.pegapista.ui.viewmodels.PostViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComentariosScreen(
    postId: String,
    remetenteId: String,
    onVoltar: () -> Unit,
    viewModel: PostViewModel = viewModel()
) {
    LaunchedEffect(postId) {
        viewModel.carregarComentarios(postId)
    }

    val comentarios by viewModel.comentariosState.collectAsState()
    var textoComentario by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    // Coluna Principal
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        // --- 1. HEADER (TOPO) ---
        Row(
            modifier = Modifier
                .height(60.dp)
                .fillMaxWidth()
                .padding(horizontal = 8.dp), // Um pouco de margem lateral
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onVoltar) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Voltar",
                    tint = MaterialTheme.colorScheme.primary // Cor azul
                )
            }
            Spacer(Modifier.width(16.dp))
            Text(
                text = "Comentários",
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 10.dp)
        ) {
            if (comentarios.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Seja o primeiro a comentar!", color = Color.Gray)
                    }
                }
            }
            items(comentarios) { comentario ->
                ItemComentario(comentario, viewModel)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .windowInsetsPadding(WindowInsets.ime.union(WindowInsets.navigationBars))
                .padding(8.dp),
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
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedContainerColor = Color(0xFFF5F5F5)
                ),
                maxLines = 3
            )

            IconButton(
                onClick = {
                    if (textoComentario.isNotBlank()) {
                        viewModel.enviarComentario(postId, remetenteId, texto = textoComentario)
                        textoComentario = ""
                        focusManager.clearFocus()
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

@Composable
fun ItemComentario(comentario: Comentario, viewModel: PostViewModel) {
    var fotoPerfilUrl by remember { mutableStateOf("") }
    LaunchedEffect(comentario.userId) {
        val url = viewModel.getFotoPerfil(comentario.userId)
        if (url != null) {
            fotoPerfilUrl = url
        }
    }
    Row(modifier = Modifier.fillMaxWidth()) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(fotoPerfilUrl)
                .crossfade(true)
                .crossfade(500)
                .diskCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .build(),
            contentDescription = "Foto do usuário",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(2.dp, Color.White, CircleShape),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.perfil_padrao),
            error = painterResource(R.drawable.perfil_padrao)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = comentario.nomeUsuario,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                val dataFormatada = try {
                    SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(Date(comentario.data))
                } catch (e: Exception) { "" }
                Text(
                    text = dataFormatada,
                    color = Color.Gray,
                    fontSize = 10.sp
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = comentario.texto,
                fontSize = 14.sp,
                color = Color.DarkGray
            )
        }
    }
}