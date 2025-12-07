package com.example.pegapista.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp

data class BottomItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

@Composable
fun BottomBar(
    currentRoute: String?,
    onItemClick: (String) -> Unit
) {
    val items = listOf(
        BottomItem("Home", "Início", Icons.Default.Home),
        BottomItem("comunidade", "Comunidade", Icons.Default.Group), // Group substitui Diversity3 (evita erros)
        BottomItem("perfil", "Perfil", Icons.Default.Person),
        BottomItem("AtividadeBefore", "Atividade", Icons.Default.DirectionsRun),
        BottomItem("notificacoes", "Notificações", Icons.Default.Notifications)
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        items.forEach { item ->
            // Lógica: Se a rota for a do item, OU se estivermos em "AtividadeAfter" e o item for "Atividade"
            val isSelected = currentRoute == item.route ||
                    (currentRoute == "AtividadeAfter" && item.route == "AtividadeBefore")

            NavigationBarItem(
                selected = isSelected,
                onClick = { onItemClick(item.route) },
                label = { Text(item.label, fontSize = 10.sp, color = Color.White) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                )
            )
        }
    }
}