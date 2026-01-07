package com.example.pegapista.ui.screens

import android.Manifest
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.pegapista.R
import com.example.pegapista.data.models.Usuario
import com.example.pegapista.ui.theme.PegaPistaTheme
import com.example.pegapista.ui.viewmodels.PerfilUsuarioViewModel
import com.example.pegapista.ui.viewmodels.PerfilViewModel
import com.example.pegapista.ui.viewmodels.PostViewModel
import org.koin.androidx.compose.koinViewModel
import java.io.File
import androidx.compose.runtime.collectAsState
import com.example.pegapista.data.models.Postagem
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PerfilScreen(
    onDeslogar: () -> Unit,
    onCommentClick: (Postagem, String) -> Unit,
    onSeguidoresClick: (String) -> Unit,
    onSeguindoClick: (String) -> Unit,
    //viewModel: PerfilViewModel = viewModel(),
    viewModel: PerfilViewModel = koinViewModel(),
    perfilviewModel: PerfilUsuarioViewModel = viewModel(),
    postsviewModel: PostViewModel = viewModel()
) {

    val usuario by viewModel.userState.collectAsState()
    val meuId = usuario.id
    val scrollState = rememberScrollState()
    val posts = perfilviewModel.postsUsuario.collectAsState().value

    LaunchedEffect(meuId) {
        if (meuId != null) {
            perfilviewModel.carregarPerfilUsuario(meuId)
        }
    }
    val pullState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val onRefresh: () -> Unit = {
        isRefreshing = true
        coroutineScope.launch {
            delay(1000)
            viewModel.carregarPerfil()
            isRefreshing = false
        }
    }
    LaunchedEffect(Unit) {
        viewModel.carregarPerfil()
    }
    PullToRefreshBox(
        state = pullState,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize(),
        indicator = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 12.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                PullToRefreshDefaults.Indicator(
                    state = pullState,
                    isRefreshing = isRefreshing,
                    containerColor = Color.White,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = {
                        viewModel.deslogar()
                        onDeslogar()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Sair",
                            tint = Color.White
                        )
                    }
                }
            }

            item {
                TopPerfil(usuario, viewModel)
                Spacer(Modifier.height(5.dp))
                MetadadosUsuarioPerfil(
                    user = usuario,
                    onSeguidoresClick = { onSeguidoresClick(usuario.id) },
                    onSeguindoClick = { onSeguindoClick(usuario.id) }
                )
                Spacer(Modifier.height(20.dp))
            }

            if (posts.isNotEmpty()) {
                item {
                    Text(
                        text = "Atividades Recentes",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(posts) { post ->
                    Box(modifier = Modifier.padding(vertical = 8.dp)) {
                        PostCard(
                            post = post,
                            data = postsviewModel.formatarDataHora(post.data),
                            currentUserId = usuario.id,
                            onLikeClick = {
                                postsviewModel.toggleCurtidaPost(post)
                                perfilviewModel.atualizarLikeNoPostLocal(post.id, usuario.id ?: "")
                            },
                            onCommentClick = {
                                onCommentClick(post, usuario.id)
                            },
                            onProfileClick = {}
                        )
                    }
                }
            } else {
                item {
                    Text(
                        text = "Nenhuma atividade ainda.",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun TopPerfil(
    user: Usuario,
    viewModel: PerfilViewModel
) {
    val context = LocalContext.current
    var mostrarOpcoes by remember { mutableStateOf(false) }
    var uriTemporaria by remember { mutableStateOf<Uri?>(null) }


    val galeriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) viewModel.atualizarFotoPerfil(uri, context)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { sucesso ->
        if (sucesso && uriTemporaria != null) {
            viewModel.atualizarFotoPerfil(uriTemporaria!!, context)
        }
    }

    val permissaoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { aceitou ->
        if (aceitou) {
            uriTemporaria = criarUriParaFoto(context)
            cameraLauncher.launch(uriTemporaria!!)
        } else {
            Toast.makeText(context, "Permissão necessária para câmera", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier.padding(top = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            val foto = user.fotoPerfilUrl
            if (!foto.isNullOrEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(foto)
                        .crossfade(true)
                        .crossfade(500)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .build(),
                    contentDescription = "Foto do usuário",
                    modifier = Modifier
                        .size(125.dp)
                        .clip(CircleShape)
                        .border(5.dp, Color.White, CircleShape),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.perfil_padrao),
                    error = painterResource(R.drawable.perfil_padrao)
                )
            } else {
                Image(
                    painterResource(R.drawable.perfil_padrao),
                    contentDescription = "Foto padrão",
                    modifier = Modifier
                        .size(125.dp)
                        .clip(CircleShape)
                        .border(5.dp, Color.White, CircleShape),
                    contentScale = ContentScale.Crop
                )
            }


            Box(
                modifier = Modifier
                    .offset(x = 5.dp, y = 5.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary)
                    .border(2.dp, Color.White, CircleShape)
                    .clickable { mostrarOpcoes = true },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Alterar foto",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(Modifier.height(10.dp))
        Text(
            user.nickname,
            fontSize = 25.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }

    if (mostrarOpcoes) {
        AlertDialog(
            onDismissRequest = { mostrarOpcoes = false },
            title = { Text("Alterar Foto de Perfil") },
            text = { Text("Como você deseja enviar a foto?") },
            confirmButton = {
                TextButton(onClick = {
                    mostrarOpcoes = false
                    galeriaLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }) {
                    Text("Galeria")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    mostrarOpcoes = false
                    permissaoLauncher.launch(Manifest.permission.CAMERA)
                }) {
                    Text("Câmera")
                }
            }
        )
    }
}

@Composable
fun BoxText(metadata: String, data: String) {
    Box(
        modifier = Modifier
            .padding(10.dp)
            .size(120.dp)
            .shadow(
                elevation = 5.dp,
                shape = RoundedCornerShape(10.dp)
            )
            .background(Color.White)
            .clip(RoundedCornerShape(10.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = metadata,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.W500,
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(5.dp))
            Text(
                text = data,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

fun formatarHoras(segundos: Long): String {
    val horas = segundos / 3600
    val minutos = (segundos % 3600) / 60
    return "%dh %02dm".format(horas, minutos)
}


fun criarUriParaFoto(context: Context): Uri {
    val arquivo = File.createTempFile(
        "foto_perfil_",
        ".jpg",
        context.cacheDir
    ).apply {
        createNewFile()
        deleteOnExit()
    }
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        arquivo
    )
}
        }
}

/*@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PerfilScreenPreview() {
    PegaPistaTheme {
        //PerfilScreen(onDeslogar = {})
    }
}
*/
