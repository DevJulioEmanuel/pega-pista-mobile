package com.example.pegapista.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pegapista.R
import com.example.pegapista.ui.theme.PegaPistaTheme
import com.example.pegapista.ui.viewmodels.PostViewModel

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

    val uiState by viewModel.uiState.collectAsState()

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
            .padding(20.dp),
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
                Image(
                    painter = painterResource(id = R.drawable.mapa_teste),
                    contentDescription = "Mapa",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                )

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

                // ... (Botão adicionar foto mantido)
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
            enabled = !uiState.isLoading, // Bloqueia se estiver carregando
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
}

// StatItem permanece igual
@Composable
fun StatItem(valor: String, unidade: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = valor, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
        Text(text = unidade, fontSize = 12.sp, color = Color.Gray)
    }
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