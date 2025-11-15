package com.angelcabrera.proyecto.navigation

import SearchScreen
import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.angelcabrera.proyecto.screens.*

@SuppressLint("ComposableDestinationInComposeScope")
@Composable
fun AppNavigation() {

    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        // AUTH
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }

        // MAIN
        composable("pairup") { PairUpScreen(navController) }
        composable("progress") { ProgressScreen(navController) }
        composable("comunidad") { ComunidadScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
        composable("search") { SearchScreen(navController) }

        // BUSCAR SESIÓN
        composable("buscarSesion") { BuscarSesionScreen(navController) }

        // SESION ACTIVA
        composable("activeSession/{sessionId}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("sessionId") ?: ""
            SesionActivaScreen(navController, id)
        }

        // HISTORIAL
        composable("sessionHistory") { SessionHistoryScreen(navController) }

        // NOTIFICACIONES
        composable("notifications") { NotificationsScreen(navController) }

        // SESIÓN PROFESOR
        composable("profesorSession/{sessionId}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("sessionId") ?: ""
            ProfesorSessionScreen(navController, id)
        }
    }
}
