package com.example.pegapista.ui.screens

import android.media.Image
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.pegapista.R
import com.example.pegapista.data.models.Usuario
import com.example.pegapista.ui.theme.PegaPistaTheme
import com.example.pegapista.ui.viewmodels.BuscaViewModel


@Composable
fun BuscarAmigosScreen(
    modifier: Modifier = Modifier.background(Color.White),
    viewModel: BuscaViewModel = viewModel(),
    onPerfilUsuarioScreen: (idUsuario: String) -> Unit
) {
    val textoBusca by viewModel.textoBusca.collectAsState()
    val usuariosEncontrados by viewModel.resultados.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
    ) {
        // --- CABEÇALHO (FIXO) ---
        Column(
            modifier = Modifier.padding(horizontal = 0.dp) // Container do cabeçalho
        ) {
            Text (
                text = "Buscar Amigos",
                modifier = Modifier.padding(12.dp),
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 0.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row {
                OutlinedTextField(
                    value = textoBusca,
                    onValueChange = { viewModel.atualizarBusca(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    placeholder = { Text("Digite o nome do atleta...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Pesquisar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium
                )
            }
            Spacer(modifier = Modifier.height(25.dp))

            Text(
                text = if (textoBusca.isBlank()) "Sugestões para você" else "Resultados",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
            )
        }

        // --- LISTA (ROLÁVEL) ---
        // Aqui está o segredo: weight(1f) faz a lista pegar TODO o espaço restante
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // <--- OBRIGATÓRIO PARA CENTRALIZAR NA TELA
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // <--- OBRIGATÓRIO PARA ROLAR
                contentPadding = PaddingValues(bottom = 80.dp) // Padding extra no final para não cortar o último item
            ) {
                if (usuariosEncontrados.isEmpty() && textoBusca.isNotEmpty()) {
                    item {
                        Text(
                            text = "Nenhum usuário encontrado.",
                            modifier = Modifier.padding(16.dp),
                            color = Color.Gray
                        )
                    }
                }

                items(usuariosEncontrados) { usuario ->
                    CardUsuario(usuario, onPerfilUsuarioScreen, viewModel)
                }
            }
        }
    }
}

@Composable
fun CardUsuario(
    usuario: Usuario,
    onPerfilUsuarioScreen: (String) -> Unit,
    viewModel: BuscaViewModel
) {
    var fotoPerfilUrl by remember { mutableStateOf("") }

    LaunchedEffect(usuario.id) {
        val url = viewModel.getFotoPerfil(usuario.id)
        if (url != null) {
            fotoPerfilUrl = url
        }
    }
    Card (
        modifier = Modifier,
        onClick = { onPerfilUsuarioScreen(usuario.id) }
    ) {
        Row (
            modifier = Modifier.fillMaxWidth().background(Color.White)
        ) {
            Spacer(Modifier.width(10.dp))
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
                    .size(50.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.White, CircleShape),
                placeholder = painterResource(R.drawable.perfil_padrao),
                error = painterResource(R.drawable.perfil_padrao)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = usuario.nickname,
                fontSize = 18.sp,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
    Spacer(Modifier.height(15.dp))
}

@Preview
@Composable
fun BuscarAmigosScreenPreview() {
    PegaPistaTheme {
        BuscarAmigosScreen(onPerfilUsuarioScreen = {})
    }
}