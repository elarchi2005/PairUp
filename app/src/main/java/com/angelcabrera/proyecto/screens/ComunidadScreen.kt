package com.angelcabrera.proyecto.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.angelcabrera.proyecto.BottomNavBar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

data class CommunityUser(
    val uid: String = "",
    val name: String = "",
    val active: Boolean = false,
    val lastMessage: String = "",
    val lastMessageTime: String = ""
)

@Composable
fun ComunidadScreen(navController: NavController) {

    val db = FirebaseFirestore.getInstance()
    var users by remember { mutableStateOf<List<CommunityUser>>(emptyList()) }

    // 🔥 Escuchar usuarios activos en tiempo real
    LaunchedEffect(Unit) {
        db.collection("users")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val mapped = snapshot.documents.map { doc ->
                        CommunityUser(
                            uid = doc.id,
                            name = doc.getString("name") ?: "",
                            active = doc.getBoolean("active") ?: false,
                            lastMessage = doc.getString("lastMessage") ?: "Sin mensajes",
                            lastMessageTime = doc.getString("lastMessageTime") ?: ""
                        )
                    }
                    users = mapped.sortedByDescending { it.active }
                }
            }
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            Text(
                "Comunidad",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(Modifier.height(20.dp))

            if (users.isEmpty()) {
                Text("Cargando usuarios...", style = MaterialTheme.typography.bodyMedium)
            } else {
                LazyColumn {
                    items(users) { user ->
                        CommunityUserCard(user = user) {
                            navController.navigate("chat/${user.uid}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CommunityUserCard(user: CommunityUser, onClick: () -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {

        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // FOTO / AVATAR
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFBEE2FF))
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(user.name, style = MaterialTheme.typography.titleMedium)

                Text(
                    text = user.lastMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = user.lastMessageTime,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )

                Spacer(Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(
                            if (user.active) Color(0xFF4CAF50) else Color(0xFFB0B0B0)
                        )
                )
            }
        }
    }
}
