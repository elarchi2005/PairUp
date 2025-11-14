package com.angelcabrera.proyecto.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfesorSessionScreen(
    navController: NavController,
    sessionId: String
) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val teacherId = auth.currentUser?.uid ?: ""

    var feedback by remember { mutableStateOf("") }
    var teacherNotes by remember { mutableStateOf("") }
    var evaluation by remember { mutableStateOf("Sin evaluar") }
    var chatMessage by remember { mutableStateOf("") }

    var chatMessages by remember { mutableStateOf(listOf<String>()) }

    // 🔥 Escuchar mensajes del chat en tiempo real
    LaunchedEffect(true) {
        db.collection("sessions")
            .document(sessionId)
            .collection("chat")
            .orderBy("timestamp")
            .addSnapshotListener { value, _ ->
                val list = value?.documents?.mapNotNull { it.getString("text") } ?: emptyList()
                chatMessages = list
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Panel del Profesor") })
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {

            // ------------------------------------
            // CÓDIGO DEL ALUMNO (simulado)
            // ------------------------------------
            Text("Vista del código del alumno", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFEFEFEF))
                        .padding(12.dp)
                ) {
                    Text("Aquí se mostraría el código del alumno en tiempo real...")
                }
            }

            Spacer(Modifier.height(20.dp))

            // ------------------------------------
            // NOTAS DEL PROFESOR
            // ------------------------------------
            Text("Notas del profesor", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = teacherNotes,
                onValueChange = { teacherNotes = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Anotaciones del profesor") }
            )

            Button(
                onClick = {
                    db.collection("sessions")
                        .document(sessionId)
                        .collection("evaluation")
                        .document("notes")
                        .set(mapOf("text" to teacherNotes))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar notas")
            }

            Spacer(Modifier.height(20.dp))

            // ------------------------------------
            // EVALUACIÓN DEL ALUMNO
            // ------------------------------------
            Text("Evaluación del alumno", style = MaterialTheme.typography.titleMedium)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val options = listOf("Excelente", "Bueno", "Regular", "Deficiente")
                options.forEach { option ->
                    FilterChip(
                        selected = evaluation == option,
                        onClick = {
                            evaluation = option
                            db.collection("sessions")
                                .document(sessionId)
                                .collection("evaluation")
                                .document("status")
                                .set(mapOf("value" to option))
                        },
                        label = { Text(option) }
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ------------------------------------
            // FEEDBACK FINAL
            // ------------------------------------
            Text("Feedback final", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = feedback,
                onValueChange = { feedback = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Comentarios finales") },
                maxLines = 4
            )

            Button(
                onClick = {
                    db.collection("sessions")
                        .document(sessionId)
                        .collection("evaluation")
                        .document("feedback")
                        .set(mapOf("text" to feedback))
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Enviar feedback", color = Color.White)
            }

            Spacer(Modifier.height(20.dp))

            // ------------------------------------
            // CHAT PRIVADO DEL PROFESOR
            // ------------------------------------
            Text("Chat del profesor", style = MaterialTheme.typography.titleMedium)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Color(0xFFEFEFEF))
                    .padding(8.dp)
            ) {
                chatMessages.forEach {
                    Text("- $it")
                }
            }

            Row {
                OutlinedTextField(
                    value = chatMessage,
                    onValueChange = { chatMessage = it },
                    modifier = Modifier.weight(1f)
                )

                Spacer(Modifier.width(8.dp))

                Button(
                    onClick = {
                        if (chatMessage.isNotBlank()) {
                            db.collection("sessions")
                                .document(sessionId)
                                .collection("chat")
                                .add(
                                    mapOf(
                                        "text" to chatMessage,
                                        "userId" to teacherId,
                                        "timestamp" to System.currentTimeMillis()
                                    )
                                )
                            chatMessage = ""
                        }
                    }
                ) {
                    Text("Enviar")
                }
            }

            Spacer(Modifier.height(20.dp))

            // ------------------------------------
            // FINALIZAR SESIÓN
            // ------------------------------------
            Button(
                onClick = {
                    db.collection("sessions")
                        .document(sessionId)
                        .update("status", "finalizada")

                    navController.navigate("pairup")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
            ) {
                Text("Finalizar sesión", color = Color.White)
            }
        }
    }
}