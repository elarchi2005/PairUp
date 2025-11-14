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

    //-------------------- FIREBASE ----------------------------

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val uid = auth.currentUser?.uid

    var userName by remember { mutableStateOf("Usuario") }

    // Cargar nombre desde Firebase
    LaunchedEffect(uid) {
        if (uid != null) {
            db.collection("users").document(uid)
                .get()
                .addOnSuccessListener { doc ->
                    userName = doc.getString("name") ?: "Usuario"
                }
        }
    }

    var testimonialText by remember { mutableStateOf("") }
    var selectedStars by remember { mutableStateOf(5) }

    var testimonialList by remember { mutableStateOf<List<Testimonial>>(emptyList()) }


    LaunchedEffect(true) {
        db.collection("testimonials")
            .addSnapshotListener { value, _ ->
                if (value != null) {
                    testimonialList = value.documents.mapNotNull { doc ->
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

    //------------------------------------------------------------


    var selectedSkill by remember { mutableStateOf<String?>(null) }
    var filterAvailableOnly by remember { mutableStateOf(false) }
    var showSkillSelector by remember { mutableStateOf(false) }

    val progress = ProgressInfo(15, 5, 300)

    val topics = listOf("Python", "Java", "JavaScript", "C#")


    val allUsers = listOf(
        User("u1", "Juan Pérez", listOf("Python"), "Intermedio", 4.5, true),
        User("u2", "Ana García", listOf("Python"), "Avanzado", 5.0, false),
        User("u3", "Luis R", listOf("Python"), "Principiante", 4.0, true)
    )

    val suggested = allUsers.filter { user ->
        val skillMatch = selectedSkill?.let { user.languages.contains(it) } ?: true
        val availabilityMatch = if (filterAvailableOnly) user.available else true
        skillMatch && availabilityMatch
    }

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

            //---------------- HEADER --------------------
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
                Text(
                    "Editar perfil",
                    modifier = Modifier
                        .clickable { navController.navigate("profile") }
                        .padding(start = 8.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Bienvenido, $userName", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Comienza una nueva sesión de programación",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            //---------------- PROGRESS CARD ---------------------
            ProgressCard(progress = progress, navController = navController)

            Spacer(modifier = Modifier.height(12.dp))

            //---------------- SUGERIDOS -------------------------
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Encuentra tu pareja de código", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    suggested.forEach { user ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(MaterialTheme.colorScheme.secondary)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(user.name, style = MaterialTheme.typography.bodyLarge)
                                Text(user.languages.joinToString(", "),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Text(
                                if (user.available) "Disponible" else "No disponible",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                        Divider()
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        OutlinedButton(
                            onClick = { showSkillSelector = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Buscar por habilidad")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = { filterAvailableOnly = !filterAvailableOnly },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (filterAvailableOnly) "Quitar filtro" else "Solo disponibles")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { navController.navigate("buscarSesion") },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Iniciar sesión", color = White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            //-------------------- TESTIMONIOS FIREBASE ------------------------

            Text("Testimonios", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            testimonialList.forEach { t ->
                TestimonialCard(t)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // AGREGAR NUEVO TESTIMONIO
            Text("Añadir Testimonio:", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = testimonialText,
                onValueChange = { testimonialText = it },
                label = { Text("Escribe tu comentario...") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Selector de estrellas
            Row {
                repeat(5) { index ->
                    Icon(
                        painter = painterResource(
                            if (index < selectedStars) android.R.drawable.btn_star_big_on
                            else android.R.drawable.btn_star_big_off
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { selectedStars = index + 1 }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (testimonialText.isNotEmpty() && uid != null) {
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
                Text("Enviar Testimonio")
            }

            Spacer(modifier = Modifier.height(20.dp))

            //---------------- TEMAS -----------------------------
            Text("Temas de interés", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                topics.forEach { topic -> TopicChip(topic) }
            }

            Spacer(modifier = Modifier.height(12.dp))

            //---------------- ACTIVIDADES -----------------------
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Actividades recientes", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column { Text("Última sesión"); Text("3 horas") }
                        Column { Text("Total de pares"); Text("10") }
                        Column { Text("Total de mins"); Text("200") }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    if (showSkillSelector) {
        AlertDialog(
            onDismissRequest = { showSkillSelector = false },
            title = { Text("Seleccionar habilidad") },
            text = {
                Column {
                    val skills = listOf("Python", "Java", "JavaScript", "C#")
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
                    ) { Text("Quitar filtro") }
                }
            },
            confirmButton = {}
        )
    }
}


//------------------------------------------------------------
//  COMPONENTES REUTILIZADOS (igual que antes)
//------------------------------------------------------------

@Composable
fun ProgressCard(progress: ProgressInfo, navController: NavController) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Progreso", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column { Text("Sesiones"); Text("${progress.sessionsCompleted}") }
                    Column { Text("Mentorías"); Text("${progress.mentorsReceived}") }
                    Column { Text("Minutos"); Text("${progress.minutes}") }
                }
            }
            Button(onClick = { navController.navigate("progress") }) {
                Text("Ver más")
            }
        }
    }
}

@Composable
fun TestimonialCard(t: Testimonial) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(t.author, style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(6.dp))
            Text(t.text, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(6.dp))
            Row {
                repeat(t.stars) {
                    Icon(
                        painter = painterResource(android.R.drawable.star_on),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TopicChip(topic: String) {
    Surface(
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .wrapContentWidth()
            .height(36.dp)
            .clickable { }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Text(topic, style = MaterialTheme.typography.bodyMedium)
        }
    }
}