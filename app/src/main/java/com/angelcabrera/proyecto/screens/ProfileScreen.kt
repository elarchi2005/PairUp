package com.angelcabrera.proyecto.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.angelcabrera.proyecto.BottomNavBar
import com.angelcabrera.proyecto.models.User
import com.angelcabrera.proyecto.ui.theme.PrimaryBlack
import com.angelcabrera.proyecto.ui.theme.White

@Composable
fun ProfileScreen(navController: NavController) {

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            Text("Perfil", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(12.dp))

            var name by remember { mutableStateOf("") }
            var level by remember { mutableStateOf("Principiante") }
            var objective by remember { mutableStateOf("") }
            var availability by remember { mutableStateOf("Lun-Vie 2pm - 5pm") }

            val suggested = listOf(
                User("u1","Juan Pérez", listOf("Python"), "Intermedio", 4.5, true),
                User("u2","Ana García", listOf("Java","Kotlin"), "Avanzado", 5.0, false)
            )

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Nivel de experiencia")
                    val levels = listOf("Principiante","Intermedio","Avanzado")
                    Row {
                        levels.forEach { lvl ->
                            val selected = lvl == level
                            Button(
                                onClick = { level = lvl },
                                colors = if (selected)
                                    ButtonDefaults.buttonColors(containerColor = PrimaryBlack)
                                else ButtonDefaults.outlinedButtonColors(),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text(lvl, color = if (selected) White else MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = objective,
                        onValueChange = { objective = it },
                        label = { Text("Objetivo") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = availability,
                        onValueChange = { availability = it },
                        label = { Text("Disponibilidad") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { navController.navigate("editProfile") },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Editar perfil", color = White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Usuarios sugeridos", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(suggested) { user ->
                    SuggestedUserItem(
                        user = user,
                        onConnect = { /* más adelante */ }
                    )
                }
            }
        }
    }
}

@Composable
fun SuggestedUserItem(user: User, onConnect: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(44.dp))

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(user.name, style = MaterialTheme.typography.bodyLarge)
                Text(user.languages.joinToString(", "), style = MaterialTheme.typography.bodyMedium)
            }

            Button(onClick = onConnect) {
                Text("Conectar")
            }
        }
    }
}

