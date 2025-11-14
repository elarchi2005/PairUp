package com.angelcabrera.proyecto.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.angelcabrera.proyecto.BottomNavBar


@Composable
fun ProgressScreen(navController: NavController) {

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize()
        ) {

            Text(
                text = "Tu progreso",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Sesiones completadas: 12")
                    Text("Horas colaboradas: 18")
                    Text("Nivel estimado: Intermedio")
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Actividades recientes",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("• Sesión con Ana - Refactorización")
                    Text("• Sesión con Pedro - Estructuras de datos")
                    Text("• Sesión con Juan - Clean Architecture")
                }
            }
        }
    }
}