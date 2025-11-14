package com.angelcabrera.proyecto

import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

@Composable
fun BottomNavBar(navController: NavController) {

    NavigationBar {

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("pairup") },
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("progress") },
            icon = { Icon(Icons.Default.BarChart, contentDescription = "Progreso") },
            label = { Text("Progreso") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("comunidad") },
            icon = { Icon(Icons.Default.Chat, contentDescription = "Comunidad") },
            label = { Text("Comunidad") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("profile") },
            icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
            label = { Text("Perfil") }
        )

    }
}