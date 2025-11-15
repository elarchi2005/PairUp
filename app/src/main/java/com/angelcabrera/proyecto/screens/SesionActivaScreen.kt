package com.angelcabrera.proyecto.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.UUID

data class ChatMessage(
    val uid: String = "",
    val text: String = "",
    val timestamp: Long = 0
)

@Composable
fun SesionActivaScreen(navController: NavController, sessionId: String) {

    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    val userId = auth.currentUser?.uid ?: ""

    var role by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var chatMessages by remember { mutableStateOf(listOf<ChatMessage>()) }

    // Cargar rol
    LaunchedEffect(Unit) {
        val userDoc = db.collection("users").document(userId).get().await()
        role = userDoc.getString("role") ?: "Driver"
    }

    // Escuchar cambios de la sesión
    LaunchedEffect(sessionId) {
        db.collection("sessions").document(sessionId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    code = snapshot.getString("code") ?: ""
                }
            }
    }

    // Escuchar chat en tiempo real
    LaunchedEffect(sessionId) {
        db.collection("sessions").document(sessionId)
            .collection("chat")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    chatMessages = snapshot.documents.map { doc ->
                        ChatMessage(
                            uid = doc.getString("uid") ?: "",
                            text = doc.getString("text") ?: "",
                            timestamp = doc.getLong("timestamp") ?: 0
                        )
                    }
                }
            }
    }

    // --- UI ---
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // HEADER
        Text("Sesión Activa", style = MaterialTheme.typography.headlineMedium)
        Text("Rol: $role", color = Color.Gray)

        Spacer(Modifier.height(16.dp))

        // --- CÓDIGO COMPARTIDO ---
        Text("Código compartido", style = MaterialTheme.typography.titleMedium)

        Box(
            Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(top = 8.dp)
                .background(Color(0xFFF4F4F4), RoundedCornerShape(12.dp))
                .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            if (role == "Driver") {
                BasicTextField(
                    value = code,
                    onValueChange = {
                        code = it
                        db.collection("sessions").document(sessionId).update("code", it)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text(
                    code.ifEmpty { "<Vacío>" },
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // --- CHAT ---
        Text("Chat", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(chatMessages) { msg ->
                val isMine = msg.uid == userId

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = if (isMine) Alignment.CenterEnd else Alignment.CenterStart
                ) {

                    Box(
                        Modifier
                            .background(
                                if (isMine) Color(0xFFDCFEDC) else Color(0xFFE8E8E8),
                                RoundedCornerShape(14.dp)
                            )
                            .padding(10.dp)
                            .widthIn(max = 260.dp)
                    ) {
                        Text(msg.text)
                    }
                }

                Spacer(Modifier.height(6.dp))
            }
        }

        Spacer(Modifier.height(8.dp))

        // --- INPUT DE MENSAJE ---
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Escribe un mensaje...") }
            )

            Spacer(Modifier.width(8.dp))

            Button(onClick = {
                if (message.isNotEmpty()) {
                    val newId = UUID.randomUUID().toString()
                    db.collection("sessions").document(sessionId)
                        .collection("chat").document(newId)
                        .set(
                            mapOf(
                                "uid" to userId,
                                "text" to message,
                                "timestamp" to System.currentTimeMillis()
                            )
                        )
                    message = ""
                }
            }) {
                Text("Enviar")
            }
        }

        Spacer(Modifier.height(16.dp))

        if (role == "Profesor") {
            Button(
                onClick = {
                    db.collection("sessions").document(sessionId)
                        .update("active", false)
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(Color.Red)
            ) {
                Text("Finalizar sesión", color = Color.White)
            }
        }
    }
}