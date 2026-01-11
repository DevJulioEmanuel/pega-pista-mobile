package com.example.pegapista.ui.screens.auth

import com.example.pegapista.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pegapista.ui.theme.PegaPistaTheme

@Composable
fun InicioScreen(
    onEntrarClick: () -> Unit,
    onCadastrarClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Image(
            painter = painterResource(R.drawable.logo__aplicativo),
            contentDescription = stringResource(R.string.desc_logo_do_aplicativo),
            modifier = Modifier
                .size(400.dp)
                .padding(top = 0.dp),
        )

        Spacer(modifier = Modifier.weight(1f))

        Card(
            shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = stringResource(R.string.label_seja_bem_vindo_a),
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 20.dp, start = 20.dp, bottom = 5.dp, end = 20.dp)
                )
                Text(
                    text = stringResource(R.string.label_registre_suas_atividades_desafie_seus_amigos_e_aproveite_cada_momento),
                    color = Color.White.copy(alpha = 0.8f),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, start = 20.dp, bottom = 5.dp, end = 20.dp)
                )
                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = onEntrarClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(top = 10.dp, start = 100.dp, end = 100.dp)
                ) {
                    Text(
                        text = stringResource(R.string.label_entrar),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Button(
                    onClick = onCadastrarClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(top = 10.dp, start = 100.dp, end = 100.dp)
                ) {
                    Text(
                        text = stringResource(R.string.label_cadastre_se),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Preview (showBackground = true)
@Composable
fun InicioScreenPreview() {
    PegaPistaTheme {
        InicioScreen(onEntrarClick = {}, onCadastrarClick = {})
    }
}