package com.angelcabrera.proyecto.screens


import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.angelcabrera.proyecto.ui.theme.PrimaryBlack
import com.angelcabrera.proyecto.ui.theme.White

@Composable
fun LoginScreen(navController: NavController) {

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Iniciar sesión", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(context, "Completa correo y contraseña", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                isLoading = true

                auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener { result ->
                        val uid = result.user?.uid ?: return@addOnSuccessListener

                        db.collection("users").document(uid)
                            .get()
                            .addOnSuccessListener { doc ->
                                isLoading = false
                                val role = doc.getString("role") ?: "Driver"

                                navController.navigate(
                                    if (role == "Profesor") "comunidad" else "pairup"
                                ) {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                    }
                    .addOnFailureListener {
                        isLoading = false
                        Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack)
        ) {
            Text(if (isLoading) "Cargando..." else "Entrar", color = White)
        }

        Spacer(Modifier.height(16.dp))

        Text(
            "Registrarse",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { navController.navigate("register") }
        )
    }
}