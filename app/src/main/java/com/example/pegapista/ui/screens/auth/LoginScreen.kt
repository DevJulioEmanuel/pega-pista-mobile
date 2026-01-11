package com.example.pegapista.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pegapista.R
import com.example.pegapista.ui.components.BotaoGoogle
import com.example.pegapista.ui.theme.BluePrimary
import com.example.pegapista.ui.theme.PegaPistaTheme
import com.example.pegapista.ui.viewmodels.AuthViewModel

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
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
            Toast.makeText(context, R.string.label_bem_vindo, Toast.LENGTH_SHORT).show()
            onEntrarHome()
            viewModel.resetState()
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
            contentDescription = stringResource(R.string.desc_logo_do_aplicativo),
            modifier = Modifier.size(200.dp)
        )
        Text(
            text = stringResource(R.string.label_login),
            fontWeight = FontWeight.SemiBold,
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(25.dp))
        Textsfields(
            value = email,
            onValueChange = {email = it},
            placeholder = stringResource(R.string.label_email)
        )
        Spacer(Modifier.height(15.dp))
        Textsfields(
            value = senha,
            onValueChange = { senha = it },
            placeholder = stringResource(R.string.label_senha)
        )
        Spacer(Modifier.height(40.dp))
        ButtonEntrar(
            onClick = {
                viewModel.login(email, senha)
            }
        )
        Spacer(Modifier.height(15.dp))
        Text(
            text = "ou",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(vertical = 5.dp)
        )
        BotaoGoogle(
            onGoogleSignInSelected = { token ->
                viewModel.loginComGoogle(token)
            },
            onError = { erro ->
                Toast.makeText(context, erro, Toast.LENGTH_SHORT).show()
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
    val isPassword = placeholder == stringResource(R.string.label_senha)
    val visualTransformation =
        if (isPassword) PasswordVisualTransformation() else VisualTransformation.None

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                fontSize = 14.sp
            )
        },
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
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
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
    ) {
        Text(stringResource(R.string.label_entrar), fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    PegaPistaTheme {
        LoginScreen(
            onEntrarHome = {}
        )
    }
}