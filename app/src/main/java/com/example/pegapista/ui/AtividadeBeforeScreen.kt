package com.example.pegapista.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pegapista.R
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

// Coordenada inicial de exemplo (São Paulo - Centro)
val defaultLocation = LatLng(-23.550520, -46.633308)

@Composable
fun AtividadeBeforeScreen(
    modifier: Modifier = Modifier,
    onStartActivity: () -> Unit = {}
) {
    // Estado da câmera do mapa
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 15f)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            // Usa a cor de fundo do tema (definida em Theme.kt como BackgroundLight/Dark)
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // 1. Logo no topo
        Image(
            painter = painterResource(id = R.drawable.logo_aplicativo),
            contentDescription = "Logo PegaPista",
            modifier = Modifier
                .width(180.dp)
                .height(80.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(30.dp))

        // 2. Card Azul Principal
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Ocupa o restante do espaço até a bottom bar
                .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
                .background(
                    // Usa a cor primária do tema (BluePrimary)
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(20.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {

                // Título
                Text(
                    text = "Tudo pronto para correr!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    // Usa a cor 'onPrimary' para garantir contraste com o fundo 'primary'
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center
                )

                // 3. Integração com Google Maps
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    shape = RoundedCornerShape(12.dp),
                    // O fundo do card interno pode ser 'surface' para contraste
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column {
                        // O Mapa
                        GoogleMap(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            cameraPositionState = cameraPositionState,
                            uiSettings = MapUiSettings(
                                zoomControlsEnabled = false,
                                myLocationButtonEnabled = false,
                                scrollGesturesEnabled = false,
                                zoomGesturesEnabled = false,
                                rotationGesturesEnabled = false,
                                tiltGesturesEnabled = false
                            )
                        ) {
                            // Marcador opcional
                            com.google.android.gms.maps.model.Marker(
                                state = MarkerState(position = defaultLocation),
                                title = "Local atual"
                            )
                        }

                        // Rodapé do Card do Mapa ("CORRIDA")
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                // Uma cor levemente diferente para o rodapé do card
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "CORRIDA",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                // Cor do texto sobre o surfaceVariant
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 1.5.sp
                            )
                        }
                    }
                }

                // 4. Botão Circular "Toque para começar"
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onStartActivity() }
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(90.dp)
                            .background(
                                // Podemos usar 'primaryContainer' ou uma variação da primária
                                // para destacar o botão sobre o fundo azul
                                color = MaterialTheme.colorScheme.secondary,
                                shape = CircleShape
                            )
                            .shadow(elevation = 10.dp, shape = CircleShape)
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
        }
    }
}
