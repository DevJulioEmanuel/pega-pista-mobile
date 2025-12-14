package com.example.pegapista.ui.screens

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pegapista.R
import com.example.pegapista.data.models.Corrida
import com.example.pegapista.ui.theme.PegaPistaTheme
import com.example.pegapista.ui.viewmodels.CorridaViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AtividadeAfterScreen(
    viewModel: CorridaViewModel = viewModel(),
    onFinishNavigation: () -> Unit = {}
) {
    val context = LocalContext.current
    val saveState by viewModel.saveState.collectAsState()

    val distanciaMetros by viewModel.distancia.observeAsState(0f)
    val tempoSegundos by viewModel.tempoSegundos.observeAsState(0L)
    val paceAtual by viewModel.pace.observeAsState("-:--")
    val isRastreando by viewModel.isRastreando.observeAsState(false)

    val distanciaKmExibicao = "%.2f".format(distanciaMetros / 1000)

    val tempoExibicao = remember(tempoSegundos) {
        val horas = tempoSegundos / 3600
        val minutos = (tempoSegundos % 3600) / 60
        val segundos = tempoSegundos % 60
        if (horas > 0) "%d:%02d:%02d".format(horas, minutos, segundos)
        else "%02d:%02d".format(minutos, segundos)
    }

    LaunchedEffect(saveState) {
        if (saveState.isSuccess) {
            Toast.makeText(context, "Corrida salva com sucesso!", Toast.LENGTH_SHORT).show()
            onFinishNavigation()
        }
        if (saveState.error != null) {
            Toast.makeText(context, saveState.error, Toast.LENGTH_LONG).show()
        }
    }


    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fine = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarse = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fine || coarse) {
            viewModel.iniciarCorrida()
        } else {
            Toast.makeText(context, "GPS necessário para rastrear", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // LOGO
        Image(
            painter = painterResource(R.drawable.logo_aplicativo),
            contentDescription = "Logo do aplicativo",
            modifier = Modifier
                .size(150.dp)
                .padding(bottom = 16.dp)
        )

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = if (isRastreando) "CORRENDO!" else "CORRIDA PAUSADA",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                BlocoDados(valor = distanciaKmExibicao, label = "Km")
                BlocoDados(valor = tempoExibicao, label = "Tempo")
                BlocoDados(valor = paceAtual, label = "Ritmo (min/km)")

                Spacer(modifier = Modifier.height(40.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    // BOTÃO PAUSAR / RETOMAR
                    Button(
                        onClick = { viewModel.toggleRastreamento() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isRastreando) Color(0xFFFF5252) else Color(0xFFFF9800) // Vermelho ou Laranja
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
                        enabled = !saveState.isLoading
                    ) {
                        if (saveState.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        } else {
                            Text("Finalizar", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BlocoDados(valor: String, label: String) {
    Surface(
        color = Color(0xFF0288D1),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .width(200.dp)
            .padding(vertical = 8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp)
        ) {
            Text(
                text = valor,
                fontSize = 32.sp,
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

@Preview(showBackground = true)
@Composable
fun AtividadeAfterScreenPreview() {
    PegaPistaTheme {
        AtividadeAfterScreen()
    }
}