package com.angelcabrera.proyecto.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.InsertComment
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfesorSessionScreen(
    navController: NavController,
    sessionId: String
) {

    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val teacherId = auth.currentUser?.uid ?: ""

    // ESTADOS
    var code by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var evaluation by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    var messages by remember { mutableStateOf(listOf<String>()) }
    var driverName by remember { mutableStateOf("Cargando...") }
    var navigatorName by remember { mutableStateOf("Cargando...") }

    // 🔥 1. Cargar información de la sesión
    LaunchedEffect(true) {
        db.collection("sessions").document(sessionId)
            .addSnapshotListener { snap, _ ->
                if (snap != null && snap.exists()) {
                    code = snap.getString("code") ?: ""
                    val driverId = snap.getString("driver") ?: ""
                    val navId = snap.getString("navigator") ?: ""

                    if (driverId.isNotEmpty()) {
                        db.collection("users").document(driverId).get()
                            .addOnSuccessListener { driverName = it.getString("name") ?: "Driver" }
                    }
                    if (navId.isNotEmpty()) {
                        db.collection("users").document(navId).get()
                            .addOnSuccessListener { navigatorName = it.getString("name") ?: "Navigator" }
                    }
                }
            }
    }

    // 🔥 2. Chat privado del profesor
    LaunchedEffect(true) {
        db.collection("sessions").document(sessionId)
            .collection("profChat")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { value, _ ->
                messages = value?.documents?.map { it.getString("text") ?: "" } ?: emptyList()
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Panel del Profesor")
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            //--------------------------------------
            // INFORMACIÓN DE LA SESIÓN
            //--------------------------------------
            Text("Participantes", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))

            Text("Driver: $driverName", fontWeight = FontWeight.Bold)
            Text("Navigator: $navigatorName")

            Spacer(Modifier.height(20.dp))

            //--------------------------------------
            // CÓDIGO DEL ALUMNO
            //--------------------------------------
            Text("Código del alumno", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                colors = CardDefaults.cardColors(Color(0xFF1E1E1E))
            ) {
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxSize()
                ) {
                    Text(
                        code.ifEmpty { "// Sin código aún..." },
                        color = Color.White
                    )
                }
            }

            Spacer(Modifier.height(25.dp))

            //--------------------------------------
            // NOTAS DEL PROFESOR
            //--------------------------------------
            Text("Notas del profesor", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Escribe tus notas...") }
            )

            Button(
                onClick = {
                    db.collection("sessions").document(sessionId)
                        .collection("evaluation")
                        .document("notes")
                        .set(mapOf("text" to notes))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar notas")
            }

            Spacer(Modifier.height(25.dp))

            //--------------------------------------
            // EVALUACIÓN
            //--------------------------------------
            Text("Evaluación del estudiante", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            val options = listOf("Excelente", "Bueno", "Regular", "Deficiente")

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                options.forEach { opt ->
                    FilterChip(
                        selected = evaluation == opt,
                        onClick = {
                            evaluation = opt
                            db.collection("sessions").document(sessionId)
                                .collection("evaluation")
                                .document("score")
                                .set(mapOf("value" to opt))
                        },
                        label = { Text(opt) }
                    )
                }
            }

            Spacer(Modifier.height(25.dp))

            //--------------------------------------
            // FEEDBACK FINAL
            //--------------------------------------
            Text("Feedback final", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = feedback,
                onValueChange = { feedback = it },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 4,
                placeholder = { Text("Escribe un feedback detallado...") }
            )

            Button(
                onClick = {
                    db.collection("sessions").document(sessionId)
                        .collection("evaluation")
                        .document("feedback")
                        .set(mapOf("text" to feedback))
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(Color.Black)
            ) {
                Text("Enviar Feedback", color = Color.White)
            }

            Spacer(Modifier.height(30.dp))

            //--------------------------------------
            // CHAT PRIVADO DEL PROFESOR
            //--------------------------------------
            Text("Chat privado del profesor", style = MaterialTheme.typography.titleMedium)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(Color(0xFFEDEDED), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                messages.forEach { msg ->
                    Text("• $msg")
                }
            }

            Spacer(Modifier.height(8.dp))

            Row {
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    modifier = Modifier.weight(1f)
                )

                Spacer(Modifier.width(8.dp))

                Button(onClick = {
                    if (message.isNotBlank()) {
                        db.collection("sessions").document(sessionId)
                            .collection("profChat")
                            .add(
                                mapOf(
                                    "text" to message,
                                    "teacherId" to teacherId,
                                    "timestamp" to System.currentTimeMillis()
                                )
                            )
                        message = ""
                    }
                }) {
                    Text("Enviar")
                }
            }

            Spacer(Modifier.height(30.dp))

            //--------------------------------------
            // FINALIZAR SESIÓN
            //--------------------------------------
            Button(
                onClick = {
                    db.collection("sessions")
                        .document(sessionId)
                        .update("active", false)

                    navController.navigate("pairup")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(Color.Red)
            ) {
                Text("Finalizar sesión", color = Color.White)
            }
        }
    }
}
