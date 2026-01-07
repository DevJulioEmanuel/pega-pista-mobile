package com.example.pegapista.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import org.koin.androidx.compose.koinViewModel
import com.example.pegapista.R
import com.example.pegapista.data.models.Postagem
import com.example.pegapista.data.models.Usuario
import com.example.pegapista.ui.viewmodels.PerfilUsuarioViewModel
import com.example.pegapista.ui.viewmodels.PostViewModel
import kotlin.let

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PerfilUsuarioScreen(
    modifier: Modifier = Modifier.background(Color.White),
    viewModel: PerfilUsuarioViewModel = koinViewModel(),
    postsviewModel: PostViewModel = koinViewModel(),
    onCommentClick: (Postagem) -> Unit,
    onSeguidoresClick: (String) -> Unit,
    onSeguindoClick: (String) -> Unit,
    idUsuario: String = ""
) {
    val usuario by viewModel.userState.collectAsState()
    val postagens by postsviewModel.feedState.collectAsState()
    val scrollState = rememberScrollState()
    val isSeguindo by viewModel.isSeguindo.collectAsState()
    val posts by viewModel.postsUsuario.collectAsState()
    val meuId = postsviewModel.meuId

    LaunchedEffect(idUsuario) {
        viewModel.carregarPerfilUsuario(idUsuario)
    }
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(35.dp))
            TopUsuarioPerfil(usuario)
        }
        item {
            Spacer(modifier = Modifier.height(5.dp))
            MetadadosUsuarioPerfil(
                user = usuario,
                onSeguidoresClick = { onSeguidoresClick(usuario.id) },
                onSeguindoClick = { onSeguindoClick(usuario.id) }
                )
        }
        if (usuario.id!=meuId) {
            item {
                Spacer(Modifier.height(30.dp))
                Button(
                    onClick = { viewModel.toggleSeguir() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSeguindo) Color.White else Color(0xFF0FDC52),
                        contentColor = if (isSeguindo) MaterialTheme.colorScheme.primary else Color.White
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .width(200.dp)
                        .height(50.dp)
                ) {
                    Text(
                        text = if (isSeguindo) "Seguindo ✓" else "Seguir",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }

        item {
            Spacer(Modifier.height(20.dp))
            if (posts.isNotEmpty()) {
                Spacer(Modifier.height(20.dp))
                Text(
                    text = "Atividades Recentes",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            } else {
                Text(
                    text = "Nenhuma atividade ainda.",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            }
        }

        items(posts) { post ->
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                PostCard(
                    post = post,
                    data = postsviewModel.formatarDataHora(post.data),
                    viewModel = postsviewModel,
                    currentUserId = meuId,
                    onLikeClick = {
                        postsviewModel.toggleCurtidaPost(post)
                        viewModel.atualizarLikeNoPostLocal(post.id, meuId ?: "")
                    },
                    onCommentClick = {
                        onCommentClick(post)
                    },
                    onProfileClick = {}
                    )
            }
        }

        item {
            Spacer(Modifier.height(50.dp))
        }
    }
    }


@Composable
fun TopUsuarioPerfil(user: Usuario) {
    Column (
        modifier = Modifier
            .padding(top = 15.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(user.fotoPerfilUrl)
                .crossfade(true)
                .crossfade(500)
                .diskCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .build(),
            contentDescription = "Foto do usuário",
            modifier = Modifier
                .size(125.dp)
                .clip(CircleShape)
                .border(5.dp, Color.White, CircleShape),
            placeholder = painterResource(R.drawable.perfil_padrao),
            error = painterResource(R.drawable.perfil_padrao)
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
fun MetadadosUsuarioPerfil(
    user: Usuario?,
    onSeguidoresClick: () -> Unit,
    onSeguindoClick: () -> Unit
) {
    val distanciaKm = user?.distanciaTotalKm ?: 0.0
    val tempoSegundos = user?.tempoTotalSegundos ?: 0L

    val distFormatada = "%.1f km".format(distanciaKm)
    val tempoFormatado = formatarUsuarioHoras(tempoSegundos)

    val ritmoMedio = if (distanciaKm > 0 && tempoSegundos > 0) {
        val minutosTotais = tempoSegundos / 60.0
        val pace = minutosTotais / distanciaKm
        "%.2f min/km".format(pace)
    } else {
        "0:00 min/km"
    }


    Column (
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(Modifier.height(15.dp))
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            Column (modifier = Modifier.clickable { onSeguidoresClick() }) {
                Text(
                    text = "Seguidores",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White
                )
                Text(
                    text = "${user?.seguidores}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
            Spacer(Modifier.width(40.dp))
            Column (modifier = Modifier.clickable { onSeguindoClick() }){
                Text(
                    text = "Seguindo",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White
                )
                Text(
                    text = "${user?.seguindo}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = "${user?.diasSeguidos} dias!",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )
        Spacer(Modifier.height(10.dp))
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
                text = "Seu recorde foi de ${user?.recordeDiasSeguidos} dias seguidos!",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.W500,
                modifier = Modifier.fillMaxWidth()
                    .padding(10.dp)
            )
        }
        Spacer(Modifier.height(35.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BoxUsuarioText("Distância Total", distFormatada)
            BoxUsuarioText("Tempo Total", tempoFormatado)
        }


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BoxUsuarioText("Tempo Ritmo", ritmoMedio)
            BoxUsuarioText("Calorias Queimadas", "${user?.caloriasQueimadas} kcal")
        }
    }
}

@Composable
fun BoxUsuarioText(metadata: String, data: String) {
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
                fontSize = 12.sp,
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


fun formatarUsuarioHoras(segundos: Long?): String {
    val horas = segundos?.div(3600)
    val minutos = (segundos?.rem(3600))?.div(60)
    return "%dh %02dm".format(horas, minutos)
}

/*
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PerfilUsuarioScreenPreview() {
    PegaPistaTheme {
        PerfilUsuarioScreen()
    }
}
 */