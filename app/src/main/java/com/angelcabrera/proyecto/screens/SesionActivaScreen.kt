package com.angelcabrera.proyecto.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
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
    var chatMessages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var message by remember { mutableStateOf("") }
    var sessionActive by remember { mutableStateOf(true) }


    LaunchedEffect(Unit) {
        val userDoc = db.collection("users").document(userId).get().await()
        role = userDoc.getString("role") ?: "Driver"
    }


    LaunchedEffect(sessionId) {
        db.collection("sessions").document(sessionId)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    sessionActive = snapshot.getBoolean("active") ?: true
                    code = snapshot.getString("code") ?: ""
                }
            }
    }


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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // 🔷 HEADER
        Text("Sesión Activa", style = MaterialTheme.typography.headlineMedium)
        Text("Rol: $role", style = MaterialTheme.typography.bodyLarge)

        Spacer(Modifier.height(12.dp))


        Text("Código compartido", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color(0xFFEFEFEF))
                .padding(12.dp)
        ) {
            if (role == "Driver") {
                BasicTextField(
                    value = code,
                    onValueChange = { newCode ->
                        code = newCode
                        db.collection("sessions")
                            .document(sessionId)
                            .update("code", newCode)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text(code.ifEmpty { "<Vacío>" })
            }
        }

        Spacer(Modifier.height(16.dp))

        // 🔥 5. CHAT
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
                    Text(
                        text = msg.text,
                        modifier = Modifier
                            .background(if (isMine) Color(0xFFD1FFD6) else Color(0xFFE1E1E1))
                            .padding(8.dp)
                            .widthIn(max = 250.dp)
                    )
                }
                Spacer(Modifier.height(6.dp))
            }
        }

        Spacer(Modifier.height(8.dp))

        Row {
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Mensaje...") }
            )
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = {
                    if (message.isNotEmpty()) {
                        val chatId = UUID.randomUUID().toString()
                        db.collection("sessions").document(sessionId)
                            .collection("chat")
                            .document(chatId)
                            .set(
                                mapOf(
                                    "uid" to userId,
                                    "text" to message,
                                    "timestamp" to System.currentTimeMillis()
                                )
                            )
                        message = ""
                    }
                }
            ) {
                Text("Enviar")
            }
        }

        Spacer(Modifier.height(12.dp))

        if (role == "Profesor") {
            Button(
                onClick = {
                    db.collection("sessions").document(sessionId)
                        .update("active", false)
                    navController.popBackStack()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Finalizar sesión", color = Color.White)
            }
        }
    }
}