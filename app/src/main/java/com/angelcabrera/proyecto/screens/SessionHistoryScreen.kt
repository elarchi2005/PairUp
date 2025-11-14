package com.angelcabrera.proyecto.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.angelcabrera.proyecto.BottomNavBar


data class SessionHistoryItem(
    val partnerName: String,
    val language: String,
    val duration: String,
    val date: String
)

@Composable
fun SessionHistoryScreen(navController: NavController) {

    val sessions = listOf(
        SessionHistoryItem("Ana García", "Python", "45 min", "Ayer"),
        SessionHistoryItem("Juan Pérez", "Kotlin", "1 hr", "Hace 3 días"),
        SessionHistoryItem("Rodrigo M", "JavaScript", "30 min", "Hace 1 semana")
    )

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            Text("Historial de sesiones", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(sessions) { session ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Compañero: ${session.partnerName}")
                            Text("Lenguaje: ${session.language}")
                            Text("Duración: ${session.duration}")
                            Text("Fecha: ${session.date}")
                        }
                    }
                }
            }
        }
    }
}