import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.angelcabrera.proyecto.models.User
import com.angelcabrera.proyecto.ui.theme.White
import com.angelcabrera.proyecto.ui.theme.PrimaryBlack
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun SearchScreen(navController: NavController) {

    val auth = FirebaseAuth.getInstance()
    val uid = auth.currentUser?.uid   // 👈 AQUI estaba tu error. Esto debe existir SIEMPRE
    val db = FirebaseFirestore.getInstance()

    var searchQuery by remember { mutableStateOf("") }
    var joinCode by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    // Datos ficticios por ahora
    val sampleUsers = listOf(
        User("1", "Carlos Ruiz", listOf("Python", "Kotlin"), "Intermedio", 4.7, true),
        User("2", "Mariana López", listOf("Java"), "Principiante", 4.2, true),
        User("3", "Daniel Ríos", listOf("JavaScript", "React"), "Avanzado", 5.0, false)
    )

    val filtered = sampleUsers.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.languages.any { lang -> lang.contains(searchQuery, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .padding(16.dp)
    ) {

        Text("Buscar programadores", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        // ---------------- BUSCADOR -----------------
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Buscar por nombre o lenguaje") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ---------------- CREAR SALA ----------------
        Button(
            onClick = {
                if (uid != null) {
                    loading = true
                    val roomId = db.collection("rooms").document().id

                    val roomData = mapOf(
                        "roomId" to roomId,
                        "hostId" to uid,
                        "active" to true,
                        "createdAt" to System.currentTimeMillis()
                    )

                    db.collection("rooms")
                        .document(roomId)
                        .set(roomData)
                        .addOnSuccessListener {
                            loading = false
                            navController.navigate("activeSession/$roomId")
                        }
                        .addOnFailureListener {
                            loading = false
                        }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack)
        ) {
            Text("Crear sala nueva", color = White)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ---------------- UNIRSE A SALA ----------------
        OutlinedTextField(
            value = joinCode,
            onValueChange = { joinCode = it },
            label = { Text("Código de sala") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (joinCode.isNotBlank()) {
                    loading = true
                    db.collection("rooms")
                        .document(joinCode)
                        .get()
                        .addOnSuccessListener { doc ->
                            loading = false
                            if (doc.exists()) {
                                navController.navigate("activeSession/$joinCode")
                            }
                        }
                        .addOnFailureListener {
                            loading = false
                        }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack)
        ) {
            Text("Unirse a sala", color = White)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ---------------- LISTA DE USUARIOS ----------------
        LazyColumn {
            items(filtered) { user ->
                UserCard(
                    user = user,
                    onInvite = {
                        if (uid != null) {
                            val roomId = db.collection("rooms").document().id

                            val roomData = mapOf(
                                "roomId" to roomId,
                                "hostId" to uid,
                                "guestId" to user.uid,
                                "active" to true,
                                "createdAt" to System.currentTimeMillis()
                            )

                            db.collection("rooms")
                                .document(roomId)
                                .set(roomData)
                                .addOnSuccessListener {
                                    navController.navigate("activeSession/$roomId")
                                }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun UserCard(user: User, onInvite: () -> Unit = {}) {
    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(user.name, style = MaterialTheme.typography.titleMedium)
            Text("Lenguajes: ${user.languages.joinToString(", ")}")
            Text("Nivel: ${user.level}")
            Text("Rating: ${user.rating}")
            Text(if (user.available) "Disponible" else "No disponible")

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { onInvite() },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack)
            ) {
                Text("Invitar a sala", color = White)
            }
        }
    }
}