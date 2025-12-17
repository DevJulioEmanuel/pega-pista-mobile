package com.example.pegapista.ui.screens

import androidx.compose.foundation.Image

import androidx.compose.foundation.background

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size


import androidx.compose.foundation.layout.* // Importa tudo de layout

import androidx.compose.foundation.rememberScrollState

import androidx.compose.foundation.shape.CircleShape

import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pegapista.ui.theme.PegaPistaTheme
import com.example.pegapista.R
import com.example.pegapista.data.models.Usuario
import com.example.pegapista.ui.theme.PegaPistaTheme
import com.example.pegapista.ui.viewmodels.PerfilViewModel

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
           modifier = Modifier
               .verticalScroll(scrollState)
                .padding(20.dp)
                .clip(RoundedCornerShape(5.dp))
                .fillMaxWidth()
                .fillMaxSize()
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.primary),

            verticalArrangement = Arrangement.Center,

            horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Spacer(modifier = Modifier.height(35.dp))
        TopPerfil(usuario)
        Spacer(modifier = Modifier.height(5.dp))
        MetadadosPerfil(usuario)
        Spacer(Modifier.height(20.dp))
    }
}

@Composable

fun TopPerfil(user: Usuario) {

    Column (

        modifier = Modifier

            .padding(top = 15.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Image(
            painterResource(R.drawable.jaco),
            contentDescription = "Foto do usuário",
            modifier = Modifier
                .size(125.dp)
                .clip(CircleShape)
                .border(5.dp, Color.White, CircleShape)
        )
        Spacer(Modifier.height(5.dp))
        Text(
            user.nickname,
            fontSize = 25.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }
}

@Composable
fun MetadadosPerfil(user: Usuario) {
    val distFormatada = "%.1f km".format(user.distanciaTotalKm)
    val tempoFormatado = formatarHoras(user.tempoTotalSegundos)
    val ritmoMedio = if (user.distanciaTotalKm > 0) {
        val minutosTotais = user.tempoTotalSegundos / 60.0
        val pace = minutosTotais / user.distanciaTotalKm
        "%.2f min/km".format(pace)
    } else "0:00 min/km"

    Column (
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Text(
            text = "${user.diasSeguidos} dias!",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )
        Spacer(Modifier.height(45.dp))
        Box(modifier = Modifier
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
                modifier = Modifier.fillMaxWidth()
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


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BoxText("Tempo Ritmo", ritmoMedio)
            BoxText("Calorias Queimadas", "${user.caloriasQueimadas} kcal")
        }
    }
}

@Composable
fun BoxText(metadata: String, data: String) {
    Box(modifier = Modifier
        .padding(10.dp)
        .size(120.dp)
        .shadow(
            elevation = 5.dp,
            shape = RoundedCornerShape(10.dp)
        )
        .background(Color.White)
        .clip(RoundedCornerShape(10.dp))
    ) {

        Column (modifier = Modifier.fillMaxSize().padding(4.dp),

            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center){
            Text(
                text = metadata,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.W500,
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


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PerfilScreenPreview() {
    PegaPistaTheme {
        PerfilScreen()
    }
}