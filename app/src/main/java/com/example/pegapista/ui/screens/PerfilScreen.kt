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
import coil.compose.AsyncImage
import com.example.pegapista.R
import com.example.pegapista.data.models.Usuario
import com.example.pegapista.ui.theme.PegaPistaTheme
import com.example.pegapista.ui.viewmodels.PerfilViewModel
import java.io.File

@Composable
fun PerfilScreen(
    modifier: Modifier = Modifier.background(Color.White),
    viewModel: PerfilViewModel = viewModel()
) {
    val usuario by viewModel.userState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.carregarPerfil()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .padding(20.dp)
            .clip(RoundedCornerShape(5.dp))
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(35.dp))

        // Passamos o viewModel para o TopPerfil poder chamar a atualização
        TopPerfil(usuario, viewModel)

        Spacer(modifier = Modifier.height(5.dp))
        MetadadosPerfil(usuario)
        Spacer(Modifier.height(20.dp))
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

    // --- LAUNCHERS (Gerenciadores de Resultado de Activity) ---

    // 1. Launcher da Galeria
    val galeriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) viewModel.atualizarFotoPerfil(uri)
    }

    // 2. Launcher da Câmera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { sucesso ->
        if (sucesso && uriTemporaria != null) {
            viewModel.atualizarFotoPerfil(uriTemporaria!!)
        }
    }

    // 3. Permissão de Câmera
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

    // --- UI DO TOPO ---
    Column(
        modifier = Modifier.padding(top = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            // A FOTO (Lógica para mostrar URL ou Jacó)
            val foto = user.fotoPerfilUrl
            if (!foto.isNullOrEmpty()) {
                AsyncImage(
                    model = foto,
                    contentDescription = "Foto do usuário",
                    modifier = Modifier
                        .size(125.dp)
                        .clip(CircleShape)
                        .border(5.dp, Color.White, CircleShape),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.jaco),
                    error = painterResource(R.drawable.jaco)
                )
            } else {
                Image(
                    painterResource(R.drawable.jaco),
                    contentDescription = "Foto padrão",
                    modifier = Modifier
                        .size(125.dp)
                        .clip(CircleShape)
                        .border(5.dp, Color.White, CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            // O BOTÃO DE CÂMERA (Clicável)
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

    // --- DIÁLOGO DE ESCOLHA (Pop-up) ---
    if (mostrarOpcoes) {
        AlertDialog(
            onDismissRequest = { mostrarOpcoes = false },
            title = { Text("Alterar Foto de Perfil") },
            text = { Text("Como você deseja enviar a foto?") },
            confirmButton = {
                TextButton(onClick = {
                    mostrarOpcoes = false
                    // Abre Galeria (apenas imagens)
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
                    // Pede permissão -> Cria Uri -> Abre Câmera
                    permissaoLauncher.launch(Manifest.permission.CAMERA)
                }) {
                    Text("Câmera")
                }
            }
        )
    }
}

@Composable
fun MetadadosPerfil(user: Usuario) {
    val distFormatada = "%.1f km".format(user.distanciaTotalKm)
    val tempoFormatado = formatarHoras(user.tempoTotalSegundos)
    val ritmoMedio = if (user.distanciaTotalKm > 0.0) {
        val minutosTotais = user.tempoTotalSegundos / 60.0
        val pace = minutosTotais / user.distanciaTotalKm
        "%.2f min/km".format(pace)
    } else "0:00 min/km"

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${user.diasSeguidos} dias!",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )
        Spacer(Modifier.height(45.dp))
        Box(
            modifier = Modifier
                .padding(horizontal = 35.dp)
                .fillMaxWidth()
                .shadow(
                    elevation = 5.dp,
                    shape = RoundedCornerShape(10.dp)
                )
                .background(Color.White)
                .clip(RoundedCornerShape(10.dp))
        ) {
            Text(
                text = "Seu recorde foi de ${user.recordeDiasSeguidos} dias seguidos!",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.W500,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp) // Adicionei padding interno para o texto não colar
            )
        }
        Spacer(Modifier.height(35.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BoxText("Distância Total", distFormatada)
            BoxText("Tempo Total", tempoFormatado)
        }

        Spacer(modifier = Modifier.height(10.dp)) // Espaçamento entre as linhas

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BoxText("Ritmo Médio", ritmoMedio)
            BoxText("Calorias", "${user.caloriasQueimadas} kcal")
        }
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
                fontSize = 14.sp, // Ajuste leve na fonte
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

// --- FUNÇÃO AUXILIAR PARA CRIAR O ARQUIVO TEMPORÁRIO ---
// (Necessário para a câmera salvar a foto em alta qualidade)
private fun criarUriParaFoto(context: Context): Uri {
    val arquivo = File.createTempFile(
        "foto_perfil_",
        ".jpg",
        context.cacheDir
    ).apply {
        createNewFile()
        deleteOnExit()
    }

    // O authority deve ser o mesmo definido no AndroidManifest (applicationId + .provider)
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        arquivo
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PerfilScreenPreview() {
    PegaPistaTheme {
        PerfilScreen()
    }
}