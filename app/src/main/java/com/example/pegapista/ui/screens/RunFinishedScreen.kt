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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.pegapista.R
import com.example.pegapista.ui.theme.PegaPistaTheme
import com.example.pegapista.ui.viewmodels.PostViewModel
import java.io.File

@Composable
fun RunFinishedScreen(
    distancia: Double = 0.00,
    tempo: String = "0:00",
    pace: String = "-:--",
    onFinishNavigation: () -> Unit = {},
    viewModel: PostViewModel = viewModel()
) {
    val context = LocalContext.current
    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }

    // Estados do ViewModel
    val uiState by viewModel.uiState.collectAsState()
    val fotoUri by viewModel.fotoSelecionadaUri.collectAsState()

    // Variáveis para Câmera/Galeria
    var mostrarOpcoesFoto by remember { mutableStateOf(false) }
    var uriTemporaria by remember { mutableStateOf<Uri?>(null) }

    // --- LAUNCHERS ---
    val galeriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) viewModel.selecionarFotoLocal(uri)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { sucesso ->
        if (sucesso && uriTemporaria != null) {
            viewModel.selecionarFotoLocal(uriTemporaria!!)
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

    // Feedback de Sucesso/Erro
    LaunchedEffect(uiState) {
        if (uiState.isSuccess) {
            Toast.makeText(context, "Compartilhado no Feed!", Toast.LENGTH_SHORT).show()
            onFinishNavigation()
        }
        if (uiState.error != null) {
            Toast.makeText(context, uiState.error, Toast.LENGTH_LONG).show()
        }
    }

    // --- LAYOUT ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primary)
            .padding(20.dp)
            .verticalScroll(rememberScrollState()), // Adicionei scroll para telas pequenas
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
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
                // --- ÁREA DA FOTO (Clicável) ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clickable { mostrarOpcoesFoto = true } // Clique na imagem abre opções
                ) {
                    if (fotoUri != null) {
                        // Foto escolhida pelo usuário
                        AsyncImage(
                            model = fotoUri,
                            contentDescription = "Foto da Corrida",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // Imagem padrão (Mapa)
                        Image(
                            painter = painterResource(id = R.drawable.mapa_teste),
                            contentDescription = "Mapa Padrão",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Ícone indicativo de "Editar" no canto
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(10.dp)
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Alterar foto",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
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

        // BOTÃO COMPARTILHAR
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

    // --- DIÁLOGO DE ESCOLHA DE FOTO ---
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
}

// --- Funções Auxiliares (StatItem e Criação de Arquivo) ---

@Composable
fun StatItem(valor: String, unidade: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = valor, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
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
        "${context.packageName}.provider", // Deve bater com o Manifest
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