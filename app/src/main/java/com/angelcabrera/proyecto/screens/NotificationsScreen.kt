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


data class NotificationItem(
    val message: String,
    val time: String,
    val type: String // "session", "message", etc.
)

@Composable
fun NotificationsScreen(navController: NavController) {

    val notifications = listOf(
        NotificationItem("Ana aceptó tu solicitud de sesión", "Hace 2 min", "session"),
        NotificationItem("Nuevo mensaje de Juan", "Hace 10 min", "message"),
        NotificationItem("Tu sesión se completó exitosamente", "Ayer", "info")
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

            Text("Notificaciones", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(notifications) { notif ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(notif.message)
                            Text(notif.time, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}