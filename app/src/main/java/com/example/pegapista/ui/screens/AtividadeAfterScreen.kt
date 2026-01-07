package com.example.pegapista.ui.screens

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pegapista.ui.theme.PegaPistaTheme
import com.example.pegapista.ui.viewmodels.CorridaViewModel
import com.google.android.gms.maps.model.LatLng
import org.koin.androidx.compose.koinViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AtividadeAfterScreen(
    viewModel: CorridaViewModel = koinViewModel(),
    onFinishActivity: (distancia: Double, tempo: String, pace: String, List<LatLng>) -> Unit,
    onCancelActivity: () -> Unit
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current


    val saveState by viewModel.saveState.collectAsState(initial = null)
    val percurso by viewModel.percurso.collectAsState()
    val distanciaMetros by viewModel.distancia.collectAsState()
    val tempoSegundos by viewModel.tempoSegundos.collectAsState()
    val paceAtual by viewModel.pace.collectAsState()
    val isRastreando by viewModel.isRastreando.collectAsState()
    val distanciaKmExibicao = "%.2f".format(distanciaMetros / 1000)

    val tempoExibicao = remember(tempoSegundos) {
        val horas = tempoSegundos / 3600
        val minutos = (tempoSegundos % 3600) / 60
        val segundos = tempoSegundos % 60
        if (horas > 0) "%d:%02d:%02d".format(horas, minutos, segundos)
        else "%02d:%02d".format(minutos, segundos)
    }

    // FUNCIONALIDADE CANCELAR CORRIDA
    var showDiscardDialog by remember { mutableStateOf(false) }
    BackHandler {
        showDiscardDialog = true
    }

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text("Cancelar corrida?") },
            text = { Text("Tem certeza que deseja cancelar a corrida?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDiscardDialog = false
                        onCancelActivity()
                        viewModel.finalizarCorrida()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) { Text("Parar Corrida") }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) { Text("Continuar Corrida") }
            }
        )
    }


    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocation = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocation = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineLocation || coarseLocation) {
            viewModel.iniciarCorrida()
        } else {
            Toast.makeText(context, "GPS necessário para rastrear", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        val permissoes = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissoes.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        permissionLauncher.launch(permissoes.toTypedArray())
    }

    LaunchedEffect(saveState) {
        if (saveState?.isSuccess == true) {
            val distanciaKm = (distanciaMetros / 1000).toDouble()
            val tempoFormatado = viewModel.formatarTempoParaString(tempoSegundos)
            onFinishActivity(distanciaKm, tempoFormatado, paceAtual, percurso)
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxWidth()
        ) {
            MapaEmTempoReal()
        }
        Spacer(Modifier.height(5.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BlocoDados(valor = distanciaKmExibicao, label = "Km")
            BlocoDados(valor = tempoExibicao, label = "Tempo")
            BlocoDados(valor = paceAtual, label = "Ritmo")
        }
        Spacer(modifier = Modifier.height(5.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { viewModel.toggleRastreamento() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRastreando) Color(0xFFFF5252) else Color(0xFFFF9800)
                ),
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = if (isRastreando) "Pausar" else "Retomar",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = {
                    viewModel.finalizarESalvarCorrida()
                          },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0FDC52)),
                shape = RoundedCornerShape(50),

            ) {
                Text("Finalizar", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(5.dp))
    }
}


@Composable
fun BlocoDados(valor: String, label: String) {
    Surface(
        color = Color(0xFF0288D1),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .width(100.dp)
            .padding(vertical = 8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp)
        ) {
            Text(
                text = valor,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.White
            )
        }
    }
}
@Preview (showBackground = true)
@Composable
fun AtividadeAfterScreenPreview() {
    PegaPistaTheme {
        //AtividadeAfterScreen(onFinishActivity = {} as (Double, String, String) -> Unit)
    }
}
