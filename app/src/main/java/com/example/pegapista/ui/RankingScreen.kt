package com.example.pegapista.ui

import android.R.attr.onClick
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.tooling.ComposeToolingApi
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pegapista.R
import com.example.pegapista.ui.theme.PegaPistaTheme

@Composable
fun RankingScreen(
    modifier: Modifier = Modifier.background(Color.White),
    onFeedScreen: () -> Unit
){
    var posição = 1
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.logo_aplicativo),
            contentDescription = "",
            modifier = Modifier.size(70.dp).align(Alignment.CenterHorizontally)
        )

        Column(
            modifier = modifier
                .fillMaxWidth(),
           // verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ){
                Button(
                    onClick = onFeedScreen,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFFFF)),
                    border = BorderStroke(2.dp, Color.Blue),
                    shape = RoundedCornerShape(50)
                ){

                    Text("Feed", color = Color.Blue, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { /* Ir para tela ranking */ },

                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF033BCC)),

                    shape = RoundedCornerShape(50)
                ){
                    Text("Ranking", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
            //Spacer(modifier = Modifier.weight(1f))

            Spacer(modifier = Modifier.height(40.dp))
            while (posição < 6){
                ItemRanking("Marina Sena", "5.0 km em 25:00 min", 5,  posição)
                posição++
            }

        }
        Spacer(modifier = Modifier.weight(1f))

        Image(
            painter = painterResource(R.drawable.podio),
            contentDescription = "PodioGeral",
            modifier = Modifier.size(3000.dp)
        )
    }


}
@Composable
fun ItemRanking(nome: String, info: String, sequencia: Int, posição: Int) {

    Surface(
        color = Color(0xFF0277BD),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 0.dp)
            .padding(6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            // Ícone do Usuário
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            //
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$posição º",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = nome,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

//                        Text(
//                            text = info,
//                            color = Color.White.copy(alpha = 0.8f),
//                            fontSize = 14.sp
//                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "$sequencia",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        painter = painterResource(R.drawable.logo_fogo),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(28.dp)

                    )

                }
            }

        }

    }

}




@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RankingScreenPreview() {
    PegaPistaTheme {
        RankingScreen(onFeedScreen = {})
    }
}