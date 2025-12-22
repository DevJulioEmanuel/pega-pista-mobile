package com.example.pegapista.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.pegapista.R
import com.example.pegapista.ui.theme.PegaPistaTheme
import com.example.pegapista.utils.showNotification

@Composable
fun AtividadeBeforeScreen(
    modifier: Modifier = Modifier,
    onStartActivity: () -> Unit = {}
) {
    val context = LocalContext.current

    // Configura o Launcher para pedir permissão (Android 13+)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Se o usuário permitiu, envia a notificação e inicia
            showNotification(context, "Atividade Iniciada!", "Bom treino, continue firme!")
            onStartActivity()
        } else {
            // Se negou, inicia sem notificação
            onStartActivity()
        }
    }

    // Função interna para validar e disparar
    val handleStartClick = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (hasPermission) {
                showNotification(context, "Atividade Iniciada!", "Bom treino, continue firme!")
                onStartActivity()
            } else {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            showNotification(context, "Atividade Iniciada!", "Bom treino, continue firme!")
            onStartActivity()
        }
    }
    //Coluna Principal
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo

        // Container Principal
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {

            TituloAtividade("Tudo pronto para correr!")

            // Área Central (Ícone ilustrativo no lugar do Mapa)
            CardIlustracaoAtividade()

            // Botão de Ação
            ButtonIniciarAtividade(onClick = { handleStartClick() })
        }
    }
}

@Composable
fun TituloAtividade(texto: String) {
    Text(
        text = texto,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onPrimary,
        textAlign = TextAlign.Center
    )
}

@Composable
fun CardIlustracaoAtividade() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Área visual (Ícone)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsRun,
                    contentDescription = "Ilustração de corrida",
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    modifier = Modifier.size(80.dp)
                )
            }

            // Rodapé do Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(12.dp)
            ) {
                Text(
                    text = "CORRIDA",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.5.sp
                )
            }
        }
    }
}

@Composable
fun ButtonIniciarAtividade(onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(90.dp)
                .shadow(
                    elevation = 10.dp,
                    shape = CircleShape
                )
                .background(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Começar",
                tint = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.size(50.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Toque para\ncomeçar",
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            lineHeight = 16.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AtividadeBeforePreview() {
    PegaPistaTheme {
        AtividadeBeforeScreen()
    }
}
