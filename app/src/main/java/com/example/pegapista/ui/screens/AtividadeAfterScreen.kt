package com.example.pegapista.ui.screens

import android.Manifest
import android.net.http.SslCertificate.saveState
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.pegapista.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
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
import com.example.pegapista.ui.theme.PegaPistaTheme
import com.example.pegapista.ui.viewmodels.CorridaViewModel


@Composable
fun AtividadeAfterScreen(
    viewModel: CorridaViewModel = viewModel(),
    onFinishActivity: (distancia: Double, tempo: String, pace: String) -> Unit
) {
    val scrollState = rememberScrollState()

    val context = LocalContext.current

    val distanciaMetros by viewModel.distancia.observeAsState(0f)
    val tempoSegundos by viewModel.tempoSegundos.observeAsState(0L)
    val paceAtual by viewModel.pace.observeAsState("-:--")
    val isRastreando by viewModel.isRastreando.observeAsState(false)
    val saveState by viewModel.saveState.collectAsState()
    val distanciaKmExibicao = "%.2f".format(distanciaMetros / 1000)

    val tempoExibicao = remember(tempoSegundos) {
        val horas = tempoSegundos / 3600
        val minutos = (tempoSegundos % 3600) / 60
        val segundos = tempoSegundos % 60
        if (horas > 0) "%d:%02d:%02d".format(horas, minutos, segundos)
        else "%02d:%02d".format(minutos, segundos)
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

    LaunchedEffect(saveState) {
        if (saveState.isSuccess) {
            val distanciaKm = (distanciaMetros / 1000).toDouble()
            val tempoFormatado = viewModel.formatarTempoParaString(tempoSegundos)

            onFinishActivity(distanciaKm, tempoFormatado, paceAtual)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

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
                    .verticalScroll(scrollState)
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {


                Text(
                    text = "Live",
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
                            val distanciaKm = (distanciaMetros / 1000).toDouble()
                            val tempoFormatado = viewModel.formatarTempoParaString(tempoSegundos)
                            onFinishActivity(distanciaKm, tempoFormatado, paceAtual)
                                  },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0FDC52)),
                        shape = RoundedCornerShape(50),

                    ) {
                        Text("Finalizar", color = Color.White, fontWeight = FontWeight.Bold)
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
@Preview (showBackground = true)
@Composable
fun AtividadeAfterScreenPreview() {
    PegaPistaTheme {
        AtividadeAfterScreen(onFinishActivity = {} as (Double, String, String) -> Unit)
    }
}
