package com.example.pegapista.ui.screens

import android.Manifest
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import org.koin.androidx.compose.koinViewModel
import coil.compose.AsyncImage
import com.example.pegapista.R
import com.example.pegapista.ui.components.SnapshotMap
import com.example.pegapista.ui.theme.PegaPistaTheme
import com.example.pegapista.ui.viewmodels.PostViewModel
import java.io.File
import com.example.pegapista.utils.MapUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.RoundCap
import com.google.maps.android.compose.*

@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun RunFinishedScreen(
    distancia: Double = 0.00,
    tempo: String = "0:00",
    pace: String = "-:--",
    caminhoPercorrido: List<LatLng> = emptyList(),
    onFinishNavigation: () -> Unit = {},
    viewModel: PostViewModel = koinViewModel()
) {

    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var mostrarOpcoesFoto by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState()
    var mapaAdicionado by remember { mutableStateOf(false) }

    var showDiscardDialog by remember { mutableStateOf(false) }
    BackHandler {
        showDiscardDialog = true
    }

    var uriTemporaria by remember { mutableStateOf<Uri?>(null) }
    val listaFotos by viewModel.fotosSelecionadasUris.collectAsState()
    val galeriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(5)
    ) { uris ->
        uris.forEach { viewModel.adicionarFoto(it) }
    }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { sucesso ->
        if (sucesso && uriTemporaria != null) {
            viewModel.adicionarFoto(uriTemporaria!!)
        }
    }

    val permissaoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { aceitou ->
        if (aceitou) {
            uriTemporaria = criarUriParaPost(context)
            cameraLauncher.launch(uriTemporaria!!)
        } else {
            Toast.makeText(context, "Permissão de câmera negada", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(uiState) {
        if (uiState.isSuccess) {
            Toast.makeText(context, "Compartilhado no Feed!", Toast.LENGTH_SHORT).show()
            onFinishNavigation()
        }
        if (uiState.error != null) {
            Toast.makeText(context, uiState.error, Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primary)
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = { showDiscardDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cancelar e Sair",
                    tint = Color.White
                )
            }
            Text(
                text = "Nova Publicação",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        Card(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 4f)
                        .clickable { mostrarOpcoesFoto = true }
                ) {
                        if (listaFotos.isNotEmpty()) {
                            val pagerState = rememberPagerState(pageCount = { listaFotos.size })

                            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                                AsyncImage(
                                    model = listaFotos[page],
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            if (listaFotos.size > 1) {
                                Row(
                                    Modifier.align(Alignment.BottomCenter).padding(8.dp),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    repeat(listaFotos.size) { iteration ->
                                        val color = if (pagerState.currentPage == iteration) Color.White else Color.White.copy(0.5f)
                                        Box(modifier = Modifier.padding(2.dp).clip(CircleShape).background(color).size(6.dp))
                                    }
                                }
                            }
                        }

                        else if (caminhoPercorrido.isNotEmpty() && !mapaAdicionado) {
                            SnapshotMap(
                                caminhoPercorrido = caminhoPercorrido,
                                context = context,
                                onSnapshotPronto = { uri ->
                                    viewModel.adicionarFoto(uri)
                                    mapaAdicionado = true
                                }
                            )
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(12.dp)
                        ) {
                            FloatingActionButton(
                                onClick = { galeriaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                                containerColor = Color.Black.copy(alpha = 0.7f),
                                contentColor = Color.White,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Add Fotos")
                            }
                        }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Corrida finalizada!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(valor = "%.2f".format(distancia), unidade = "Km")
                    StatItem(valor = tempo, unidade = "Duração")
                    StatItem(valor = pace, unidade = "Pace")
                }

                Spacer(modifier = Modifier.height(20.dp))

                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    OutlinedTextField(
                        value = titulo,
                        onValueChange = { titulo = it },
                        placeholder = { Text("Dê um título...", fontSize = 14.sp, color = Color.LightGray) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = descricao,
                        onValueChange = { descricao = it },
                        placeholder = { Text("Descreva como foi...", fontSize = 14.sp, color = Color.LightGray) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))


        Button(
            onClick = {
                viewModel.compartilharCorrida(
                    titulo = titulo,
                    descricao = descricao,
                    distancia = distancia,
                    tempo = tempo,
                    pace = pace
                )
            },
            enabled = !uiState.isLoading,
            modifier = Modifier.fillMaxWidth(0.8f).height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.primary)
            } else {
                Text(
                    text = "Compartilhar",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }

    if (mostrarOpcoesFoto) {
        AlertDialog(
            onDismissRequest = { mostrarOpcoesFoto = false },
            title = { Text("Adicionar Foto") },
            text = { Text("Escolha uma foto para sua corrida:") },
            confirmButton = {
                TextButton(onClick = {
                    mostrarOpcoesFoto = false
                    galeriaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }) { Text("Galeria") }
            },
            dismissButton = {
                TextButton(onClick = {
                    mostrarOpcoesFoto = false
                    permissaoLauncher.launch(Manifest.permission.CAMERA)
                }) { Text("Câmera") }
            }
        )
    }

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text("Sair sem postar?") },
            text = { Text("Se você sair agora, essa corrida ficará salva no seu histórico, mas não será publicada no Feed.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDiscardDialog = false
                        onFinishNavigation()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) { Text("Sair") }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) { Text("Continuar Editando") }
            }
        )
    }
}


@Composable
fun StatItem(valor: String, unidade: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = valor,
            fontWeight = FontWeight.Bold,
            fontSize = if (valor.length > 5) 17.sp else 20.sp,
            color = Color.Black,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Visible
        )
        Text(text = unidade, fontSize = 12.sp, color = Color.Gray)
    }
}

private fun criarUriParaPost(context: Context): Uri {
    val arquivo = File.createTempFile(
        "foto_corrida_",
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

@Preview(showBackground = true)
@Composable
fun PreviewRunFinished() {
    PegaPistaTheme {
        RunFinishedScreen(
            distancia = 5.2,
            tempo = "00:30:00",
            pace = "05:45"
        )
    }
}