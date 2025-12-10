package com.example.pegapista.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pegapista.R
import com.example.pegapista.ui.theme.BluePrimary
import com.example.pegapista.ui.theme.PegaPistaTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun CadastroScreen(modifier: Modifier = Modifier,
                   onCadastroSucesso: () -> Unit) {
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var confirmarSenha by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()


    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 20.dp), // Padding lateral geral
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 40.dp) // Espaço vertical para não colar nas bordas
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(15.dp)
                )
                .padding(vertical = 30.dp, horizontal = 20.dp), // Padding interno do card
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.logo_aplicativo),
                contentDescription = "Logo do aplicativo",
                modifier = Modifier.size(150.dp) // Reduzi um pouco para caber mais campos
            )

            Text(
                text = "Crie sua conta",
                fontWeight = FontWeight.SemiBold,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(25.dp))

            CampoCadastro(
                value = nome,
                onValueChange = { nome = it },
                label = "Nome Completo"
            )

            Spacer(Modifier.height(15.dp))

            CampoCadastro(
                value = email,
                onValueChange = { email = it },
                label = "E-mail"
            )

            Spacer(Modifier.height(15.dp))

            CampoCadastro(
                value = senha,
                onValueChange = { senha = it },
                label = "Senha",
                isPassword = true
            )

            Spacer(Modifier.height(15.dp))

            CampoCadastro(
                value = confirmarSenha,
                onValueChange = { confirmarSenha = it },
                label = "Confirmar Senha",
                isPassword = true
            )

            Spacer(Modifier.height(40.dp))

            ButtonCadastrar(
                onClick = {

                    if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                        Toast.makeText(context, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                        return@ButtonCadastrar
                    }

                    if (senha != confirmarSenha) {
                        Toast.makeText(context, "As senhas não coincidem!", Toast.LENGTH_SHORT).show()
                        return@ButtonCadastrar
                    }

                    if (senha.length < 6) {
                        Toast.makeText(context, "A senha deve ter pelo menos 6 caracteres", Toast.LENGTH_SHORT).show()
                        return@ButtonCadastrar
                    }

                    // 2. Criar utilizador no Firebase Auth
                    isLoading = true
                    auth.createUserWithEmailAndPassword(email, senha)
                        .addOnSuccessListener { authResult ->

                            val userId = authResult.user?.uid

                            if (userId != null) {
                                // Cria um "mapa" de dados para salvar
                                val usuarioMap = hashMapOf(
                                    "nome" to nome,
                                    "email" to email,
                                    "uid" to userId
                                )

                                // Salva na coleção "usuarios", no documento com o ID do utilizador
                                db.collection("usuarios").document(userId)
                                    .set(usuarioMap)
                                    .addOnSuccessListener {
                                        isLoading = false
                                        Toast.makeText(context, "Conta criada com sucesso!", Toast.LENGTH_LONG).show()

                                        onCadastroSucesso()
                                    }
                                    .addOnFailureListener { e ->
                                        isLoading = false
                                        Toast.makeText(context, "Erro ao salvar dados: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }
                        .addOnFailureListener { exception ->
                            isLoading = false

                            Toast.makeText(context, "Erro no cadastro: ${exception.message}", Toast.LENGTH_LONG).show()
                        }
                }
            )
        }
    }
}
@Composable
fun CampoCadastro(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false
) {
    val visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(text = label, fontSize = 14.sp)
        },
        label = {
            Text(text = label)
        },
        visualTransformation = visualTransformation,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFFCCCCCC),
            unfocusedBorderColor = Color(0xFFDDDDDD),
            focusedPlaceholderColor = Color(0xFFBBBBBB),
            unfocusedPlaceholderColor = Color(0xFFBBBBBB),
            focusedContainerColor = Color(0xFFFDFDFD),
            unfocusedContainerColor = Color(0xFFFDFDFD),
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier
            .height(60.dp)
            .fillMaxWidth(),
        singleLine = true
    )
}

@Composable
fun ButtonCadastrar(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(12.dp),
                clip = false
            )
            .height(50.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = BluePrimary
        )
    ) {
        Text("Cadastrar", fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true)
@Composable
fun CadastroScreenPreview() {
    PegaPistaTheme {
        CadastroScreen(
            onCadastroSucesso = {}
        )
    }
}
