package com.example.pegapista.ui.screens

import android.widget.Toast
import com.example.pegapista.R
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pegapista.ui.theme.PegaPistaTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.pegapista.data.Corrida
@Composable
fun AtividadeAfterScreen(
    distancia: Double = 3.50,
    tempo: String = "28:30",
    pace: String = "5:45",
    onFinishNavigation: () -> Unit = {}
) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()


    var isSaving by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // LOGO
        Image(
            painter = painterResource(R.drawable.logo_aplicativo),
            contentDescription = "Logo do aplicativo",
            modifier = Modifier
                .size(150.dp) // Ajustei um pouco o tamanho
                .padding(bottom = 16.dp)
        )


        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            // CONTEÚDO DENTRO DO CARD
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {


                Text(
                    text = "Live",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 20.dp)
                )


                BlocoDados(valor = "3.50", label = "Km")
                BlocoDados(valor = "28:30", label = "min")
                BlocoDados(valor = "5:45", label = "ritmo médio atual")

                Spacer(modifier = Modifier.height(40.dp))


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    Button(
                        onClick = { /* Ação de Pausar */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("Pausar", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    // Botão Finalizar (Verde)
                    Button(
                        onClick = { val user = auth.currentUser
                            if (user != null && !isSaving) {
                                isSaving = true


                                val corridaId = db.collection("corridas").document().id


                                val novaCorrida = Corrida(
                                    id = corridaId,
                                    userId = user.uid,
                                    distanciaKm = distancia,
                                    tempo = tempo,
                                    pace = pace
                                    // A data é gerada automaticamente na classe Corrida
                                )


                                db.collection("corridas").document(corridaId)
                                    .set(novaCorrida)
                                    .addOnSuccessListener {
                                        isSaving = false
                                        Toast.makeText(context, "Corrida salva com sucesso!", Toast.LENGTH_SHORT).show()

                                        onFinishNavigation()
                                    }
                                    .addOnFailureListener { e ->
                                        isSaving = false
                                        Toast.makeText(context, "Erro ao salvar: ${e.message}", Toast.LENGTH_LONG).show()
                                    }
                            } else {
                                Toast.makeText(context, "Erro: Usuário não logado", Toast.LENGTH_SHORT).show()
                            }},
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0FDC52)),
                        shape = RoundedCornerShape(50),
                        enabled = !isSaving
                    ) {
                        Text("Finalizar", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun BlocoDados(valor: String, label: String) {
    Surface(
        color = Color(0xFF0288D1),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .width(200.dp)
            .padding(vertical = 8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp)
        ) {
            Text(
                text = valor,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.White
            )
        }
    }
}
@Preview (showBackground = true)
@Composable
fun AtividadeAfterScreenPreview() {
    PegaPistaTheme {
        AtividadeAfterScreen()
    }
}
