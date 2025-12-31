package com.example.pegapista.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.isEmpty
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import com.example.pegapista.R
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pegapista.data.models.Usuario
import com.example.pegapista.ui.viewmodels.RankingViewModel

@Composable
fun RankingScreen(
    modifier: Modifier = Modifier.background(Color.White),
    onFeedScreen: () -> Unit,
    onBuscarAmigosScreen: () -> Unit,
    viewModel: RankingViewModel = viewModel()
){
    val ranking by viewModel.ranking.collectAsState()
    Column(
        modifier = modifier.fillMaxSize().background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .height(60.dp)
                .fillMaxWidth()
        ){
            Text(
                text = "Comunidade",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 12.dp),
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pesquisar amigos",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(onClick = { onBuscarAmigosScreen() }) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Pesquisar Amigos",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

        }


        Column(
            modifier = modifier
                .fillMaxWidth(),
            // verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = onFeedScreen,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFFFF)),
                    border = BorderStroke(2.dp, Color.Gray),
                    shape = RoundedCornerShape(50)
                ) {

                    Text(
                        "Feed",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Button(
                    onClick = { },

                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),

                    shape = RoundedCornerShape(50)
                ) {
                    Text("Ranking", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
            //Spacer(modifier = Modifier.weight(1f))

            Spacer(modifier = Modifier.height(40.dp))

            if (ranking.isEmpty()) {
                Text(
                    "Siga amigos para ver o ranking!",
                    color = Color.Gray,
                    modifier = Modifier.padding(20.dp)
                )
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    itemsIndexed(ranking) { index, usuario ->
                        ItemRanking(
                            nome = usuario.nickname,
                            sequencia = usuario.diasSeguidos,
                            posição = index + 1
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(R.drawable.podio),
            contentDescription = "PodioGeral",
            modifier = Modifier.size(200.dp) // CORREÇÃO: Valor razoável em vez de 3000.dp
        )
    }
}

@Composable
fun ItemRanking(nome: String, sequencia: Int, posição: Int) { // CORREÇÃO: Removido 'info'
    Surface(
        color = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "$posição º",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.width(45.dp)
            )

            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(35.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = nome,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$sequencia",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    painter = painterResource(R.drawable.logo_fogo),
                    contentDescription = null,
                    tint = Color.Unspecified, // Mantém as cores originais do seu PNG/Vector
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

