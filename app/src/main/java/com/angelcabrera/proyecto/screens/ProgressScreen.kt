package com.angelcabrera.proyecto.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.angelcabrera.proyecto.BottomNavBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

data class UserProgress(
    val sessions: Int = 0,
    val minutes: Int = 0,
    val level: String = "Principiante",
    val recent: List<String> = emptyList()
)

@Composable
fun ProgressScreen(navController: NavController) {

    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val uid = auth.currentUser?.uid ?: ""

    var progress by remember { mutableStateOf(UserProgress()) }
    var loading by remember { mutableStateOf(true) }

    // 🔥 Cargar datos en tiempo real
    LaunchedEffect(uid) {
        db.collection("sessions")
            .whereEqualTo("active", false)                      // solo sesiones cerradas
            .whereArrayContainsAny("participants", listOf(uid)) // usuario participó
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->

                if (snapshot == null) return@addSnapshotListener

                var totalMinutes = 0
                val recentList = mutableListOf<String>()

                snapshot.documents.forEach { doc ->
                    val mins = (doc.getLong("minutes") ?: 0L).toInt()
                    totalMinutes += mins

                    val driver = doc.getString("driver") ?: "?"
                    val navigator = doc.getString("navigator") ?: "?"
                    val title = "Driver: $driver - Navigator: $navigator"

                    recentList.add(title)
                }

                val sessionCount = snapshot.size()

                val level = when {
                    sessionCount >= 25 -> "Avanzado"
                    sessionCount >= 10 -> "Intermedio"
                    else -> "Principiante"
                }

                progress = UserProgress(
                    sessions = sessionCount,
                    minutes = totalMinutes,
                    level = level,
                    recent = recentList.take(10)
                )

                loading = false
            }
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize()
                .background(Color.White)
        ) {

            Text(
                text = "Tu progreso",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(Modifier.height(20.dp))

            if (loading) {
                CircularProgressIndicator()
                return@Column
            }

            // MÉTRICAS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricCard("Sesiones", "${progress.sessions}", Icons.Default.CheckCircle, Color(0xFF4CAF50))
                MetricCard("Minutos", "${progress.minutes}", Icons.Default.Timer, Color(0xFFFF9800))
                MetricCard("Nivel", progress.level, Icons.Default.History, Color(0xFF2196F3))
            }

            Spacer(Modifier.height(30.dp))

            Text("Progreso general", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(10.dp))

            val percent = (progress.sessions / 25f).coerceAtMost(1f)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(22.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE0E0E0))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(percent)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF673AB7))
                )
            }

            Spacer(Modifier.height(40.dp))

            Text("Actividades recientes", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))

            progress.recent.forEach { item ->
                ActivityItem(item)
                Spacer(Modifier.height(6.dp))
            }
        }
    }
}


@Composable
fun MetricCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Card(
        modifier = Modifier
            .width(110.dp)
            .height(120.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(30.dp))
            Spacer(Modifier.height(6.dp))
            Text(value, fontWeight = FontWeight.Bold)
            Text(title, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
fun ActivityItem(text: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth()
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50))
            Spacer(Modifier.width(10.dp))
            Text(text, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
