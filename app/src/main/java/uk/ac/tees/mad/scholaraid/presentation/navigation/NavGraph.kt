package uk.ac.tees.mad.scholaraid.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import uk.ac.tees.mad.scholaraid.presentation.auth.AuthScreen
import uk.ac.tees.mad.scholaraid.presentation.profile_setup.ProfileSetupScreen
import uk.ac.tees.mad.scholaraid.presentation.splash.SplashScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        // Splash Screen
        composable(route = Screen.Splash.route) {
            SplashScreen(navController = navController)
        }

        // Auth Screen (Login/Register)
        composable(route = Screen.Auth.route) {
            AuthScreen(navController = navController)
        }

        // Profile Setup Screen
        composable(route = Screen.ProfileSetup.route) {
            ProfileSetupScreen(navController = navController)
        }

       composable(Screen.Main.route) {
           MainScreen(mainNavController = navController)
       }
    }
}