package com.angelcabrera.proyecto.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.angelcabrera.proyecto.BottomNavBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class AppNotification(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val type: String = "general",
    val timestamp: Long = 0
)

@Composable
fun NotificationsScreen(navController: NavController) {

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val uid = auth.currentUser?.uid ?: ""

    var notifications by remember { mutableStateOf<List<AppNotification>>(emptyList()) }

    // 🔥 LISTEN FIRESTORE
    LaunchedEffect(true) {
        db.collection("users")
            .document(uid)
            .collection("notifications")
            .orderBy("timestamp")
            .addSnapshotListener { snap, _ ->
                if (snap != null) {
                    notifications = snap.documents.map {
                        AppNotification(
                            id = it.id,
                            title = it.getString("title") ?: "Notificación",
                            message = it.getString("message") ?: "",
                            type = it.getString("type") ?: "general",
                            timestamp = it.getLong("timestamp") ?: 0
                        )
                    }
                }
            }
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->

        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            Text(
                "Notificaciones",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(Modifier.height(16.dp))

            if (notifications.isEmpty()) {
                Text(
                    "No tienes notificaciones",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(notifications) { n ->
                        NotificationCard(n)
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationCard(notification: AppNotification) {

    val icon = when (notification.type) {
        "warning" -> Icons.Default.Warning
        "session" -> Icons.Default.Person
        else -> Icons.Default.Notifications
    }

    val color = when (notification.type) {
        "warning" -> Color(0xFFFFC107)
        "session" -> Color(0xFF03A9F4)
        else -> Color(0xFF9C27B0)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(notification.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    notification.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}
