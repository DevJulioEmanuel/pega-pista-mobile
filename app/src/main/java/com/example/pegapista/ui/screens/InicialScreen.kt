package com.example.pegapista.ui.screens

import com.example.pegapista.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pegapista.ui.theme.PegaPistaTheme

@Composable
fun InicialScreen(){
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
      //  verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Image(
            painter = painterResource(R.drawable.logo_aplicativo),
            contentDescription = "Logo do aplicativo",
            modifier = Modifier
                .size(400.dp)
                .padding(top = 0.dp, ),

        )
        Spacer(modifier = Modifier.weight(1f))

        }
    }



@Preview (showBackground = true)
@Composable
fun InicialScreenPreview() {
    PegaPistaTheme {
        InicialScreen()
    }
}