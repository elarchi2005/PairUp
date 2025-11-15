package com.angelcabrera.proyecto.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.angelcabrera.proyecto.BottomNavBar
import com.angelcabrera.proyecto.models.ProgressInfo
import com.angelcabrera.proyecto.models.Testimonial
import com.angelcabrera.proyecto.models.User
import com.angelcabrera.proyecto.ui.theme.PrimaryBlack
import com.angelcabrera.proyecto.ui.theme.White
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun PairUpScreen(navController: NavController) {

    // ------------------ FIREBASE ------------------
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val uid = auth.currentUser?.uid

    var userName by remember { mutableStateOf("Usuario") }

    // Nombre del usuario logueado
    LaunchedEffect(uid) {
        if (uid != null) {
            db.collection("users").document(uid)
                .get()
                .addOnSuccessListener { doc ->
                    userName = doc.getString("name") ?: "Usuario"
                }
        }
    }

    // ------------------ ESTADO FILTROS ------------------
    var selectedSkill by remember { mutableStateOf<String?>(null) }
    var filterAvailableOnly by remember { mutableStateOf(false) }
    var showSkillSelector by remember { mutableStateOf(false) }

    // Progreso “dummy” por ahora
    val progress = ProgressInfo(12, 3, 250)

    // Usuarios recomendados (dummy por ahora)
    val allUsers = listOf(
        User("u1", "Juan Pérez", listOf("Python", "Kotlin"), "Intermedio", 4.5, true),
        User("u2", "Ana García", listOf("Java"), "Avanzado", 4.9, false),
        User("u3", "Luis R", listOf("JavaScript", "React"), "Principiante", 4.0, true),
        User("u4", "María P", listOf("Python"), "Intermedio", 4.7, true)
    )

    val recommendedUsers = allUsers.filter { u ->
        val skillOk = selectedSkill?.let { sk -> u.languages.any { it.equals(sk, ignoreCase = true) } } ?: true
        val availabilityOk = if (filterAvailableOnly) u.available else true
        skillOk && availabilityOk
    }

    // ------------------ TESTIMONIOS (FIRESTORE) ------------------
    var testimonialText by remember { mutableStateOf("") }
    var selectedStars by remember { mutableStateOf(5) }
    var testimonials by remember { mutableStateOf<List<Testimonial>>(emptyList()) }

    // Escuchar cambios en la colección de testimonios
    LaunchedEffect(true) {
        db.collection("testimonials")
            .addSnapshotListener { value, _ ->
                if (value != null) {
                    testimonials = value.documents.mapNotNull { doc ->
                        Testimonial(
                            id = doc.id,
                            author = doc.getString("author") ?: "",
                            text = doc.getString("text") ?: "",
                            stars = (doc.getLong("stars") ?: 5).toInt()
                        )
                    }
                }
            }
    }

    // ------------------ UI ------------------
    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(White)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            // HEADER
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.secondary)
                )

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text("Bienvenido, $userName", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Encuentra tu pareja de código ideal",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }

                Text(
                    text = "Editar perfil",
                    modifier = Modifier
                        .clickable { navController.navigate("profile") }
                        .padding(start = 8.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(20.dp))

            // PROGRESS CARD
            ProgressCard(progress = progress, navController = navController)

            Spacer(Modifier.height(20.dp))

            // RECOMMENDED USERS
            Text("Recomendados", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(10.dp))

            if (recommendedUsers.isEmpty()) {
                Text("No hay usuarios disponibles.")
            } else {
                recommendedUsers.forEach { u ->
                    UserRecommendationCard(u)
                    Spacer(Modifier.height(8.dp))
                }
            }

            Spacer(Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { showSkillSelector = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Filtrar por habilidad")
                }
                OutlinedButton(
                    onClick = { filterAvailableOnly = !filterAvailableOnly },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (filterAvailableOnly) "Todos" else "Solo disponibles")
                }
            }

            Spacer(Modifier.height(18.dp))

            Button(
                onClick = { navController.navigate("buscarSesion") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(PrimaryBlack)
            ) {
                Text("Buscar / iniciar sesión", color = White)
            }

            Spacer(Modifier.height(26.dp))

            // TESTIMONIOS
            Text("Testimonios", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            if (testimonials.isEmpty()) {
                Text("Aún no hay testimonios. ¡Sé el primero en dejar uno!")
            } else {
                testimonials.forEach {
                    TestimonialCard(it)
                    Spacer(Modifier.height(6.dp))
                }
            }

            Spacer(Modifier.height(12.dp))

            Text("Añadir testimonio", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))

            OutlinedTextField(
                value = testimonialText,
                onValueChange = { testimonialText = it },
                label = { Text("Escribe tu comentario...") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Row {
                repeat(5) { index ->
                    Icon(
                        painter = painterResource(
                            if (index < selectedStars)
                                android.R.drawable.btn_star_big_on
                            else
                                android.R.drawable.btn_star_big_off
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { selectedStars = index + 1 }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    if (testimonialText.isNotBlank() && uid != null) {
                        val newTestimonial = hashMapOf(
                            "author" to userName,
                            "text" to testimonialText,
                            "stars" to selectedStars
                        )
                        db.collection("testimonials").add(newTestimonial)
                        testimonialText = ""
                        selectedStars = 5
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Publicar testimonio")
            }

            Spacer(Modifier.height(30.dp))
        }
    }

    // DIALOG FILTRO POR HABILIDAD
    if (showSkillSelector) {
        AlertDialog(
            onDismissRequest = { showSkillSelector = false },
            title = { Text("Seleccionar habilidad") },
            text = {
                Column {
                    val skills = listOf("Python", "Java", "JavaScript", "Kotlin", "React")
                    skills.forEach { skill ->
                        Text(
                            text = skill,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable {
                                    selectedSkill = skill
                                    showSkillSelector = false
                                }
                        )
                    }
                    TextButton(
                        onClick = {
                            selectedSkill = null
                            showSkillSelector = false
                        }
                    ) {
                        Text("Quitar filtro")
                    }
                }
            },
            confirmButton = {}
        )
    }
}

// ---------------------------------------------------------
// COMPONENTES REUTILIZADOS
// ---------------------------------------------------------

@Composable
fun ProgressCard(progress: ProgressInfo, navController: NavController) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Tu progreso", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column {
                        Text("Sesiones", style = MaterialTheme.typography.labelMedium)
                        Text("${progress.sessionsCompleted}")
                    }
                    Column {
                        Text("Mentorías", style = MaterialTheme.typography.labelMedium)
                        Text("${progress.mentorsReceived}")
                    }
                    Column {
                        Text("Minutos", style = MaterialTheme.typography.labelMedium)
                        Text("${progress.minutes}")
                    }
                }
            }
            Button(onClick = { navController.navigate("progress") }) {
                Text("Ver más")
            }
        }
    }
}

@Composable
fun UserRecommendationCard(user: User) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.secondary)
            )

            Spacer(Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(user.name, style = MaterialTheme.typography.bodyLarge)
                Text(
                    "Lenguajes: ${user.languages.joinToString(", ")}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Text(
                    "Nivel: ${user.level} | ★ ${user.rating}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text(
                text = if (user.available) "Disponible" else "Ocupado",
                style = MaterialTheme.typography.labelSmall,
                color = if (user.available) Color(0xFF2E7D32) else Color.Gray
            )
        }
    }
}

@Composable
fun TestimonialCard(t: Testimonial) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(t.author.ifBlank { "Anónimo" }, style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(4.dp))
            Text(t.text, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(6.dp))
            Row {
                repeat(t.stars) {
                    Icon(
                        painter = painterResource(android.R.drawable.star_on),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFFFFC107)
                    )
                }
            }
        }
    }
}