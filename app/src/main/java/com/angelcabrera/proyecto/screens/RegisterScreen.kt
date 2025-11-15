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
fun RegisterScreen(navController: NavController) {

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("Driver") }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Registro", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

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

        Spacer(Modifier.height(12.dp))

        // Selección de rol
        Row {
            listOf("Driver", "Navigator", "Profesor").forEach { r ->
                Text(
                    r,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { role = r },
                    color = if (role == r) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
                    Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener { result ->
                        val uid = result.user?.uid ?: return@addOnSuccessListener

                        val data = mapOf(
                            "uid" to uid,
                            "name" to name,
                            "email" to email,
                            "role" to role,
                            "languages" to listOf<String>()
                        )

                        db.collection("users").document(uid).set(data)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Cuenta creada", Toast.LENGTH_SHORT).show()
                                navController.navigate("login")
                            }
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack)
        ) {
            Text("Registrarse", color = White)
        }

        Spacer(Modifier.height(16.dp))

        Text(
            "¿Ya tienes cuenta? Inicia sesión",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { navController.navigate("login") }
        )
    }
}