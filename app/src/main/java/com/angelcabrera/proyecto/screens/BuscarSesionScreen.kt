package com.angelcabrera.proyecto.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import java.util.UUID

@Composable
fun BuscarSesionScreen(navController: NavController) {

    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    val userId = auth.currentUser?.uid ?: ""
    var userRole by remember { mutableStateOf("") }
    var sesiones by remember { mutableStateOf<List<Pair<String, Map<String, Any>>>>(emptyList()) }


    LaunchedEffect(Unit) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { doc ->
                userRole = doc.getString("role") ?: ""
            }
    }

    // 2. Cargar sesiones activas según rol
    LaunchedEffect(userRole) {
        if (userRole.isNotEmpty()) {
            db.collection("sessions")
                .whereEqualTo("active", true)
                .get()
                .addOnSuccessListener { snapshot ->
                    val lista = snapshot.documents.map { doc ->
                        doc.id to (doc.data ?: emptyMap<String, Any>())
                    }
                    sesiones = lista
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Text("Buscar sesión", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        if (sesiones.isEmpty()) {
            Text("No hay sesiones disponibles.")
        } else {
            sesiones.forEach { (id, data) ->

                val driver = data["driver"] as? String
                val navigator = data["navigator"] as? String
                val prof = data["professor"] as? String


                val disponible = when (userRole) {
                    "Driver" -> navigator == null || navigator == ""
                    "Navigator" -> navigator == null || navigator == ""
                    "Profesor" -> prof == null || prof == ""
                    else -> false
                }

                if (disponible) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(Modifier.padding(12.dp)) {

                            Text("Sesión ID: $id")
                            Spacer(Modifier.height(4.dp))
                            Text("Driver: ${driver ?: "Vacante"}")
                            Text("Navigator: ${navigator ?: "Vacante"}")
                            Text("Profesor: ${prof ?: "Vacante"}")

                            Spacer(Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    unirUsuarioASesion(
                                        db = db,
                                        sessionId = id,
                                        userId = userId,
                                        role = userRole
                                    ) {
                                        navController.navigate("activeSession/$id")
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Unirse a sesión")
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // BOTÓN PARA CREAR UNA NUEVA SESIÓN
        Button(
            onClick = {
                crearSesionNueva(
                    db = db,
                    userId = userId,
                    role = userRole
                ) { sessionId ->
                    navController.navigate("activeSession/$sessionId")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Crear nueva sesión")
        }
    }
}






fun unirUsuarioASesion(
    db: FirebaseFirestore,
    sessionId: String,
    userId: String,
    role: String,
    onSuccess: () -> Unit
) {
    val field = when (role) {
        "Driver" -> "driver"
        "Navigator" -> "navigator"
        "Profesor" -> "professor"
        else -> ""
    }

    if (field.isNotEmpty()) {
        db.collection("sessions").document(sessionId)
            .update(field, userId)
            .addOnSuccessListener { onSuccess() }
    }
}






fun crearSesionNueva(
    db: FirebaseFirestore,
    userId: String,
    role: String,
    onSuccess: (String) -> Unit
) {
    val sessionId = UUID.randomUUID().toString()

    val data = mutableMapOf(
        "active" to true,
        "createdAt" to FieldValue.serverTimestamp(),
        "driver" to null,
        "navigator" to null,
        "professor" to null
    )

    data[role.lowercase()] = userId

    db.collection("sessions").document(sessionId)
        .set(data)
        .addOnSuccessListener {
            onSuccess(sessionId)
        }
}
