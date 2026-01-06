package com.example.pegapista.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import com.example.pegapista.R
import com.example.pegapista.data.models.Postagem
import com.example.pegapista.data.models.Usuario
import com.example.pegapista.ui.viewmodels.PerfilUsuarioViewModel
import com.example.pegapista.ui.viewmodels.PostViewModel
// Certifique-se de importar o PostCard se ele estiver em outro arquivo
// import com.example.pegapista.ui.components.PostCard

@Composable
fun PerfilUsuarioScreen(
    modifier: Modifier = Modifier.background(Color.White),
    viewModel: PerfilUsuarioViewModel = koinViewModel(),
    postsviewModel: PostViewModel = koinViewModel(), // CORREÇÃO: Vírgula adicionada e uso do koinViewModel
    onCommentClick: (Postagem) -> Unit,
    idUsuario: String = ""
) {
    val usuario by viewModel.userState.collectAsState()
    // val postagens by postsviewModel.feedState.collectAsState() // Não parecia estar sendo usado aqui, mas se precisar pode descomentar
    val isSeguindo by viewModel.isSeguindo.collectAsState()
    val posts by viewModel.postsUsuario.collectAsState()
    val meuId = postsviewModel.meuId

    LaunchedEffect(idUsuario) { // CORREÇÃO: Adicionei idUsuario na chave para recarregar se o ID mudar
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
            MetadadosUsuarioPerfil(usuario)
        }
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
                // Certifique-se que o componente PostCard existe no seu projeto
                PostCard(
                    post = post,
                    data = postsviewModel.formatarDataHora(post.data),
                    currentUserId = meuId,
                    onLikeClick = {
                        postsviewModel.toggleCurtidaPost(post)
                        // Se você criou a função atualizarLikeNoPostLocal no ViewModel, descomente abaixo:
                        // viewModel.atualizarLikeNoPostLocal(post.id, meuId ?: "")
                    },
                    onCommentClick = {
                        onCommentClick(post)
                    },
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
        Image(
            painterResource(R.drawable.jaco), // Certifique-se que esta imagem existe
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
fun MetadadosUsuarioPerfil(user: Usuario) {
    val distFormatada = "%.1f km".format(user.distanciaTotalKm)
    val tempoFormatado = formatarUsuarioHoras(user.tempoTotalSegundos)
    val ritmoMedio = if (user.distanciaTotalKm > 0) {
        val minutosTotais = user.tempoTotalSegundos / 60.0
        val pace = minutosTotais / user.distanciaTotalKm
        "%.2f min/km".format(pace)
    } else "0:00 min/km"

    Column (
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(Modifier.height(15.dp))
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            Column {
                Text(
                    text = "Seguidores",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White
                )
                Text(
                    text = "${user.seguidores}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
            Spacer(Modifier.width(40.dp))
            Column {
                Text(
                    text = "Seguindo",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White
                )
                Text(
                    text = "${user.seguindo}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = "${user.diasSeguidos} dias!",
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
                text = "Seu recorde foi de ${user.recordeDiasSeguidos} dias seguidos!",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.W500,
                modifier = Modifier.fillMaxWidth()
                    .padding(10.dp) // Adicionei padding interno para ficar mais bonito
            )
        }
        Spacer(Modifier.height(35.dp))

        // CORREÇÃO: Usando BoxUsuarioText em vez de BoxText (que não estava definido)
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
            BoxUsuarioText("Calorias Queimadas", "${user.caloriasQueimadas} kcal")
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
                fontSize = 12.sp, // Ajustei ligeiramente o tamanho
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

fun formatarUsuarioHoras(segundos: Long): String {
    val horas = segundos / 3600
    val minutos = (segundos % 3600) / 60
    return "%dh %02dm".format(horas, minutos)
}