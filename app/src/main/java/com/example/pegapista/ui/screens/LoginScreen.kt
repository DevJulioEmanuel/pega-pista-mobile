package com.example.pegapista.ui

import android.widget.Toast
import android.util.Log
import com.example.pegapista.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.* // Importa tudo de layout
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pegapista.ui.theme.BluePrimary
import com.example.pegapista.ui.theme.PegaPistaTheme
import com.example.pegapista.ui.viewmodels.AuthViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier, // Removi o background default aqui para controlar melhor abaixo
    onVoltarClick: () -> Unit,
    onEntrarHome: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(uiState) {
        if (uiState.error != null) {
            Toast.makeText(context, uiState.error, Toast.LENGTH_LONG).show()
        }
        if (uiState.isSuccess) {
            Toast.makeText(context, "Bem-vindo!", Toast.LENGTH_SHORT).show()
            onEntrarHome()
            viewModel.resetState() // Limpa o estado para não rodar de novo ao voltar
        }
    }

    Column (


    // 1. Estado da rolagem para telas pequenas
//    val scrollState = rememberScrollState()

    // 2. Box Principal (Fundo Azul) que ocupa a tela toda
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center // Centraliza o cartão branco no meio da tela
    ) {
        // 3. Coluna do Cartão Branco (Agora com Scroll)
        Column(
            modifier = Modifier
                .padding(20.dp) // Margem externa para não colar nas bordas
                .widthIn(max = 400.dp) // Opcional: Evita que o cartão fique muito esticado em tablets
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(15.dp)
                )
                .padding(20.dp) // Padding interno do cartão (respiro para os elementos)
                .verticalScroll(scrollState), // HABILITA O SCROLL AQUI
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.logo_aplicativo),
                contentDescription = "Logo do aplicativo",
                modifier = Modifier.size(150.dp) // Reduzi um pouco (200 era muito grande para telas pequenas)
            )
            Text(
                text = "Login",
                fontWeight = FontWeight.SemiBold,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(25.dp))

            // Campos de texto
            Textsfields(
                value = email,
                onValueChange = { email = it },
                placeholder = "Email"
            )
            Spacer(Modifier.height(15.dp))
            Textsfields(
                value = senha,
                onValueChange = { senha = it },
                placeholder = "Senha"
            )
            Spacer(Modifier.height(40.dp))

            ButtonEntrar(
                onClick = {
                    if (email.isNotEmpty() && senha.isNotEmpty()) {
                        isLoading = true
                        Log.d("LOGIN", "Tentando logar com: $email")

                        auth.signInWithEmailAndPassword(email, senha)
                            .addOnSuccessListener {
                                isLoading = false
                                Log.d("LOGIN", "Sucesso! Indo para a home")
                                Toast.makeText(context, "Bem-vindo de volta!", Toast.LENGTH_SHORT).show()
                                onEntrarHome()
                            }
                            .addOnFailureListener { exception ->
                                isLoading = false
                                Log.e("LOGIN", "Erro: ${exception.message}")
                                Toast.makeText(context, "Erro: Verifique e-mail e senha", Toast.LENGTH_LONG).show()
                            }

                    } else {
                        Toast.makeText(context, "Os campos não podem estar vazios", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            )
        }
            .padding(start = 40.dp, end = 40.dp, top = 100.dp, bottom = 100.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(15.dp)
            ),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Image(
            painter = painterResource(R.drawable.logo_aplicativo),
            contentDescription = "Logo do aplicativo",
            modifier = Modifier.size(200.dp)
        )
        Text(
            text = "Login",
            fontWeight = FontWeight.SemiBold,
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(25.dp))
        Textsfields(
            value = email,
            onValueChange = {email = it},
            placeholder = "Email"
        )
        Spacer(Modifier.height(15.dp))
        Textsfields(
            value = senha,
            onValueChange = { senha = it },
            placeholder = "Senha"
        )
        Spacer(Modifier.height(40.dp))
        ButtonEntrar(
            onClick = {
                viewModel.login(email, senha)
            }
        )
    }
}

@Composable
fun Textsfields(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    val isPassword = placeholder == "Senha"
    val visualTransformation =
        if (isPassword) PasswordVisualTransformation() else VisualTransformation.None

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                fontSize = 14.sp
            )},
        label = {
            Text(text = placeholder)
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
            .fillMaxWidth() // 4. MUDANÇA: Ocupa a largura disponível no pai
            .padding(horizontal = 10.dp) // Um pequeno respiro lateral
    )
}

@Composable
fun ButtonEntrar(
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .padding(horizontal = 30.dp) // Mantém o padding lateral do botão
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(12.dp),
                clip = false
            )
            .height(50.dp)
            .fillMaxWidth(), // Ocupa a largura disponível
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = BluePrimary
        )
    ) {
        Text("Entrar", fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    PegaPistaTheme {
        LoginScreen(
            onVoltarClick = {},
            onEntrarHome = {}
        )
    }
}