package com.example.pegapista.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.ModeComment
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import coil.compose.AsyncImage
import com.example.pegapista.data.models.Postagem
import com.example.pegapista.ui.theme.PegaPistaTheme
import com.example.pegapista.ui.viewmodels.PostViewModel
import compartilharPost


@Composable
fun FeedScreen(
    modifier: Modifier = Modifier.background(Color.White),
    onRankingScreen: () -> Unit,
    onBuscarAmigosScreen: () -> Unit,
    onCommentClick: (Postagem) -> Unit,
    viewModel: PostViewModel = koinViewModel()
) {
    val postagens by viewModel.feedState.collectAsState()
    val meuId = viewModel.meuId
    val qtdComentarios = viewModel.comentariosState.collectAsState().value.size
    var showDeleteDialog by remember { mutableStateOf(false) }
    var postDeleteId by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        viewModel.carregarFeed()
    }

    Column(
        modifier = Modifier.background(Color.White)
    ) {
        Box(
            modifier = Modifier.height(60.dp).fillMaxWidth()
        ){
            Text (
                text = "Comunidade",
                modifier = Modifier.align(Alignment.CenterStart).padding(start = 12.dp),
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pesquisar amigos",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(onClick = { onBuscarAmigosScreen() }) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Pesquisar Amigos",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

        }

        Column(
            modifier = modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ){
                Button(
                    onClick = { viewModel.carregarFeed() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(50)
                ){
                    Text("Feed", color = Color.White, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onRankingScreen,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    border = BorderStroke(2.dp, Color.Gray),
                    shape = RoundedCornerShape(50)
                ){
                    Text("Ranking", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }
            if (postagens.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhuma corrida ainda...", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(postagens) { post ->
                        PostCard(
                            post = post,
                            data = viewModel.formatarDataHora(post.data),
                            currentUserId = meuId,
                            onLikeClick = {
                                viewModel.toggleCurtidaPost(post)
                            },
                            onCommentClick = {
                                onCommentClick(post)
                            },
                            onDeleteClick = { postId ->
                                showDeleteDialog = true
                                postDeleteId = postId
                            }
                        )
                    }
                }
            }
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Excluir atividade?") },
                    text = { Text("Essa ação não pode ser desfeita.") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                postDeleteId?.let { id ->
                                    viewModel.excluirPost(id)
                                }
                                showDeleteDialog = false
                            },
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                        ) { Text("Excluir") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
                    }
                )
            }

        }
    }
}

@Composable
fun PostCard(
    post: Postagem,
    data: String = "",
    onLikeClick: () -> Unit,
    onCommentClick: (Postagem) -> Unit,
    currentUserId: String,
    onDeleteClick: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val euCurti = post.curtidas.contains(currentUserId)
    val qtdCurtidas = post.curtidas.size
    val qtdComentarios = post.qtdComentarios
    val isUsuario = post.userId == currentUserId

    val listaImagens = post.urlsFotos

    var menuExpandido by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
    ) {
        Column (
            modifier = Modifier
                .background(Color.White)
                .padding(10.dp)
                .fillMaxWidth()
        ){
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "",
                    modifier = Modifier.size(45.dp),
                    tint = Color.Gray
                )
                Spacer(Modifier.width(5.dp))
                Column (
                    modifier = Modifier.weight(1f)
                ){
                    Text(
                        text=post.autorNome,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.DarkGray
                    )
                    Text(
                        text=data,
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                }
                Box {
                    IconButton(onClick = { menuExpandido = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Opções",
                            tint = Color.Gray
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpandido,
                        onDismissRequest = { menuExpandido = false },
                        containerColor = Color.White
                    ) { DropdownMenuItem(
                            text = { Text("Compartilhar") },
                            leadingIcon = {
                                Icon(Icons.Default.Share, contentDescription = null)
                            },
                            onClick = {
                                menuExpandido = false
                                compartilharPost(context, post)
                            }
                        )
                        if (isUsuario) {
                            DropdownMenuItem(
                                text = { Text("Excluir") },
                                leadingIcon = {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = null)
                                },
                                onClick = {
                                    menuExpandido = false
                                    onDeleteClick(post.id)
                                }
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(15.dp))
            Column {
                Text(
                    text = post.titulo,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.DarkGray
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    text = post.descricao,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.DarkGray
                )
                Spacer(Modifier.height(12.dp))
                Row {
                    metadadosCorrida("%.2f km".format(post.corrida.distanciaKm), "Distância")
                    Spacer(Modifier.width(50.dp))
                    metadadosCorrida(post.corrida.tempo, "Tempo")
                    Spacer(Modifier.width(50.dp))
                    metadadosCorrida(post.corrida.pace, "Ritmo")
                }
            }
            Spacer(Modifier.height(15.dp))
            if (listaImagens.isNotEmpty()) {
                val pagerState = rememberPagerState(pageCount = { listaImagens.size })

                Box {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(4f / 4f)
                    ) { page ->
                        AsyncImage(
                            model = listaImagens[page],
                            contentDescription = "Foto da atividade",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    if (listaImagens.size > 1) {
                        Row(
                            Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 10.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            repeat(listaImagens.size) { iteration ->
                                val color = if (pagerState.currentPage == iteration) Color.White else Color.White.copy(alpha = 0.5f)
                                Box(
                                    modifier = Modifier
                                        .padding(3.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .size(8.dp)
                                )
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(

            ) {
                IconButton(onClick = onLikeClick) {
                    Icon(
                        imageVector = if (euCurti) {
                            Icons.Filled.Favorite
                        } else {
                            Icons.Filled.FavoriteBorder
                        },
                        contentDescription = "Like",
                        tint = Color.DarkGray
                    )
                }
                Text(
                    text = "$qtdCurtidas",
                    color = Color.DarkGray,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                IconButton( onClick = { onCommentClick(post) } ) {
                    Icon(
                        imageVector = Icons.Outlined.ModeComment,
                        contentDescription = "Commentary",
                        tint = Color.DarkGray
                    )
                }
                Text(
                    text = "$qtdComentarios",
                    color = Color.DarkGray,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 0.dp),
                thickness = 0.8.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }
       }
}

@Composable
fun metadadosCorrida(dado: String, metadado: String) {
    Column {
        Text(
            text=metadado,
            fontSize = 12.sp,
            color = Color.DarkGray
        )
        Text(
            text=dado,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color.DarkGray
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FeedScreenPreview() {
    PegaPistaTheme {
        FeedScreen(onRankingScreen = {}, onBuscarAmigosScreen = {}, onCommentClick = {})
    }
}