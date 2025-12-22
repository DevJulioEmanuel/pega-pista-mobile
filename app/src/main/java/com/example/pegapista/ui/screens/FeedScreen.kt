package com.example.pegapista.ui.screens

import android.widget.Button
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ModeComment
import androidx.compose.material.icons.outlined.ModeComment
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pegapista.data.models.Postagem
import com.example.pegapista.ui.theme.PegaPistaTheme
import com.example.pegapista.R
import com.example.pegapista.ui.viewmodels.PostViewModel


@Composable
fun FeedScreen(
    modifier: Modifier = Modifier.background(Color.White),
    onRankingScreen: () -> Unit,
    viewModel: PostViewModel = viewModel()
) {
    val postagens by viewModel.feedState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.carregarFeed()
    }


    Column(
        modifier = Modifier.background(Color.White)
    ) {
        Image(
            painter = painterResource(R.drawable.logo_aplicativo),
            contentDescription = "",
            modifier = Modifier.size(70.dp).align(Alignment.CenterHorizontally)
        )

        Column(
            modifier = modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ){
                Button(
                    onClick = { viewModel.carregarFeed() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(50)
                ){
                    Text("Feed", color = Color.White, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onRankingScreen,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFFFF)),
                    border = BorderStroke(2.dp, Color.Blue),
                    shape = RoundedCornerShape(50)
                ){
                    Text("Ranking", color = Color.Blue, fontWeight = FontWeight.Bold)
                }
            }
            if (postagens.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhuma corrida ainda...", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(postagens) { post ->
                        PostCard(post)
                    }
                }
            }


        }
    }
}

@Composable
fun PostCard(post: Postagem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
    ) {
        Column (
            modifier = Modifier
                .background(Color.White)
                .padding(10.dp)
                .fillMaxWidth()
        ){
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "",
                    modifier = Modifier.size(45.dp),
                    tint = Color.Gray
                )
                Spacer(Modifier.width(5.dp))
                Column {
                    Text(
                        text=post.autorNome,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.DarkGray
                    )
                    Text(
                        text="Atleta PegaPista",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                }
            }
            Spacer(Modifier.height(15.dp))
            Column {
                Text(
                    text = post.titulo,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.DarkGray
                )
                Spacer(Modifier.height(12.dp))
                Row {
                    metadadosCorrida("%.2f km".format(post.corrida.distanciaKm), "Distância")
                    Spacer(Modifier.width(50.dp))
                    metadadosCorrida(post.corrida.tempo, "Tempo")
                    Spacer(Modifier.width(50.dp))
                    metadadosCorrida(post.corrida.pace, "Ritmo")
                }
            }
            Spacer(Modifier.height(15.dp))
            Image(
                painter = painterResource(R.drawable.mapa_teste),
                contentDescription = "Imagem do Mapa",
                modifier = Modifier
                    .fillMaxWidth()
                    .size(200.dp)

            )
            Spacer(Modifier.height(8.dp))
            Row() {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Filled.FavoriteBorder,
                        contentDescription = "Like",
                        tint = MaterialTheme.colorScheme.primary
                    )

                }
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Outlined.ModeComment,
                        contentDescription = "Commentary",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 0.dp),
                thickness = 0.8.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }
       }
}

@Composable
fun metadadosCorrida(dado: String, metadado: String) {
    Column {
        Text(
            text=metadado,
            fontSize = 12.sp,
            color = Color.DarkGray
        )
        Text(
            text=dado,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color.DarkGray
        )
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FeedScreenPreview() {
    PegaPistaTheme {
        FeedScreen(onRankingScreen = {})
    }
}