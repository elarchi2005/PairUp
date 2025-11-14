package com.angelcabrera.proyecto.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.angelcabrera.proyecto.BottomNavBar


data class ChatUser(
    val name: String,
    val lastMessageTime: String,
    val active: Boolean
)

@Composable
fun ComunidadScreen(navController: NavController) {

    val chats = listOf(
        ChatUser("Juan Pérez", "2:00 PM", true),
        ChatUser("Ana García", "3:15 PM", false),
        ChatUser("Pedro R", "11:40 AM", true)
    )

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Text("Chat / Comunidad", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(chats) { user ->
                    ChatItem(user = user)
                }
            }
        }
    }
}

@Composable
fun ChatItem(user: ChatUser) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { /* navegar a chat individual más adelante */ },
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Avatar simple
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(20.dp)
                    )
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(user.name, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = if (user.active) "Activo" else "Inactivo",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (user.active)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Text(
                text = user.lastMessageTime,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}