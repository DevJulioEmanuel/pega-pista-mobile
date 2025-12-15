package com.example.pegapista.ui



import androidx.compose.foundation.Image

import androidx.compose.foundation.background

import androidx.compose.foundation.border

import androidx.compose.foundation.layout.* // Importa tudo de layout

import androidx.compose.foundation.rememberScrollState

import androidx.compose.foundation.shape.CircleShape

import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.foundation.verticalScroll

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

import com.example.pegapista.R

import com.example.pegapista.data.Usuario

import com.example.pegapista.ui.theme.PegaPistaTheme



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



// 1. Criamos o estado da rolagem

    val scrollState = rememberScrollState()



//    Column(
//
//        modifier = Modifier
//
//            .background(Color.White)
//
//            .fillMaxSize() // Garante que a tela base ocupe tudo
//
//            .verticalScroll(scrollState) // 2. Habilita a rolagem vertical
//
//    ) {





        Column(

            modifier = modifier
                .padding(20.dp)
                .clip(RoundedCornerShape(5.dp))
              //  .fillMaxWidth()
                .fillMaxSize()
                //.fillMaxHeight()
                .background(MaterialTheme.colorScheme.primary),

           // verticalArrangement = Arrangement.Center,

            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Spacer(modifier = Modifier.height(35.dp))

            TopPerfil(usuarioExemplo)

            Spacer(modifier = Modifier.height(5.dp))


            MetadadosPerfil(usuarioExemplo)

// Adicionei um Spacer no final para dar um respiro no fundo do cartão azul

            Spacer(Modifier.height(20.dp))

        }

    }

//}



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

        modifier = Modifier.fillMaxWidth(), // Ajuste para largura total

        horizontalAlignment = Alignment.CenterHorizontally

    ){



        Text(

            text = "${user.DiasSeguidos} dias!",

            fontSize = 20.sp,

            fontWeight = FontWeight.ExtraBold,

            color = Color.White

        )

        Spacer(Modifier.height(45.dp))

        Box(modifier = Modifier

            .padding(horizontal = 35.dp) // Simplifiquei o padding

            .fillMaxWidth()

            .shadow(

                elevation = 5.dp,

                shape = RoundedCornerShape(10.dp)

            )

            .background(Color.White)

            .clip(RoundedCornerShape(10.dp))

            .padding(10.dp) // Padding interno para o texto não colar na borda

        ) {

            Text(

                text = "Seu recorde foi de ${user.RecordDiasSeguidos} dias seguidos!",

                textAlign = TextAlign.Center,

                fontWeight = FontWeight.W500,

                modifier = Modifier.fillMaxWidth()

            )

        }

        Spacer(Modifier.height(35.dp))



// Linha 1 de Estatísticas

        Row(

            modifier = Modifier.fillMaxWidth(),

            horizontalArrangement = Arrangement.SpaceEvenly // Distribui melhor os itens

        ) {

            BoxText("Distância Total", user.DistanciaTotal)

            BoxText("Tempo Total", user.TempoTotal)

        }



// Linha 2 de Estatísticas

        Row(

            modifier = Modifier.fillMaxWidth(),

            horizontalArrangement = Arrangement.SpaceEvenly

        ) {

            BoxText("Tempo Ritmo", user.TempoRitmo)

            BoxText("Calorias Queimadas", user.CaloriasQueimadas)

        }

    }

}



@Composable

fun BoxText(metadata: String, data: String) {

// Mantive a estrutura, mas é importante que o tamanho não seja fixo demais

// Se quiseres ainda mais responsividade, podemos usar 'weight' no futuro.

    Box(modifier = Modifier

        .padding(10.dp)

        .size(120.dp) // Cuidado: em telas muito estreitas, 120+120+paddings pode cortar.

        .shadow(

            elevation = 5.dp,

            shape = RoundedCornerShape(10.dp)

        )

        .background(Color.White)

        .clip(RoundedCornerShape(10.dp))

    ) {

        Column (modifier = Modifier.fillMaxSize().padding(4.dp), // Padding interno extra

            horizontalAlignment = Alignment.CenterHorizontally,

            verticalArrangement = Arrangement.Center){

            Text(

                text = metadata,

                textAlign = TextAlign.Center,

                fontWeight = FontWeight.W500,

                fontSize = 14.sp, // Reduzi levemente a fonte para garantir que cabe

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

