package com.angelcabrera.proyecto.navigation

import PairUpScreen
import SearchScreen
import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.angelcabrera.proyecto.screens.BuscarSesionScreen
import com.angelcabrera.proyecto.screens.ComunidadScreen


import com.angelcabrera.proyecto.screens.ProfileScreen
import com.angelcabrera.proyecto.screens.LoginScreen
import com.angelcabrera.proyecto.screens.NotificationsScreen
import com.angelcabrera.proyecto.screens.ProfesorSessionScreen
import com.angelcabrera.proyecto.screens.ProgressScreen
import com.angelcabrera.proyecto.screens.RegisterScreen
import com.angelcabrera.proyecto.screens.SesionActivaScreen
import com.angelcabrera.proyecto.screens.SessionHistoryScreen

@SuppressLint("ComposableDestinationInComposeScope")
@Composable
fun AppNavigation() {

    val navController: NavHostController = rememberNavController()


    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }

        // Pantallas principales con bottom bar
        composable("pairup") { PairUpScreen(navController) }
        composable("progress") { ProgressScreen(navController) }
        composable("comunidad") { ComunidadScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
        composable("search") { SearchScreen(navController) }

        // Secundarias sin bottom bar

        composable("buscarSesion") { BuscarSesionScreen(navController) }

        // Sesión activa
        composable("activeSession/{sessionId}") { navBackStackEntry ->
            val id = navBackStackEntry.arguments?.getString("sessionId") ?: ""
            SesionActivaScreen(navController, id)
            composable("sessionHistory") { SessionHistoryScreen(navController) }
            composable("notifications") { NotificationsScreen(navController) }
            composable("profesorSession/{sessionId}") { backStack ->
                val id = backStack.arguments?.getString("sessionId") ?: ""
                ProfesorSessionScreen(navController, id)
            }
        }
    }}