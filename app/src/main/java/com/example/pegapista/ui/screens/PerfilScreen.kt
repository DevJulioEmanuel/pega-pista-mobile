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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.pegapista.ui.theme.PegaPistaTheme
import com.example.pegapista.R
import com.example.pegapista.data.Usuario

@Composable
fun PerfilScreen(modifier: Modifier = Modifier.background(Color.White)) {
    val usuarioExemplo = Usuario(
        nickname = "Daniel Jacó",
        DistanciaTotal = "312 km",
        TempoTotal = "01:15:30",
        TempoRitmo = "06:02 min/km",
        CaloriasQueimadas = "850 kcal",
        DiasSeguidos = 3,
        RecordDiasSeguidos = 6
    )
    Column(
        modifier = Modifier.background(Color.White)
    ) {
        Image(
            painter = painterResource(R.drawable.logo_aplicativo),
            contentDescription = "",
            modifier = Modifier.size(125.dp).align(Alignment.CenterHorizontally)
        )

        Column(
            modifier = modifier
                .padding(start = 20.dp, end = 20.dp, bottom = 40.dp)
                .clip(RoundedCornerShape(20.dp))
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopPerfil(usuarioExemplo)
            Spacer(Modifier.height(10.dp))
            MetadadosPerfil(usuarioExemplo)
        }
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
    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Text(
            text = "${user.DiasSeguidos} dias!",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )
        Spacer(Modifier.height(15.dp))
        Box(modifier = Modifier
            .padding(start = 35.dp, end = 35.dp)
            .fillMaxWidth()
            .shadow(
                elevation = 5.dp,
                shape = RoundedCornerShape(10.dp)
            )
            .background(Color.White)
            .clip(RoundedCornerShape(10.dp))
        ) {
            Text(
                text = "Seu recorde foi de ${user.RecordDiasSeguidos} dias seguidos!",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.W500,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(Modifier.height(10.dp))
        Row {
            BoxText("Distância Total", user.DistanciaTotal)
            BoxText("Tempo Total", user.TempoTotal)
        }
        Row {
            BoxText("Tempo Ritmo", user.TempoRitmo)
            BoxText("Calorias Queimadas", user.CaloriasQueimadas)
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
        Column (modifier = Modifier.fillMaxSize(),
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


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PerfilScreenPreview() {
    PegaPistaTheme {
        PerfilScreen()
    }
}