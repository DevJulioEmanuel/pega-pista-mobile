package com.example.pegapista.ui.screens

import android.widget.Toast
import android.util.Log
import com.example.pegapista.R
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
    modifier: Modifier = Modifier.background(MaterialTheme.colorScheme.primary),
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
        modifier = modifier
            .fillMaxSize()
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
            Text(
                text = placeholder
            )
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
            .width(255.dp),
    )
}

@Composable
fun ButtonEntrar(
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .padding(horizontal = 30.dp)
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
    )  {
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