package uk.ac.tees.mad.scholaraid.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import uk.ac.tees.mad.scholaraid.presentation.auth.AuthScreen
import uk.ac.tees.mad.scholaraid.presentation.splash.SplashScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(route = Screen.Splash.route) {
            SplashScreen(navController = navController)
        }

        composable(route = Screen.Auth.route) {
            AuthScreen(navController = navController)
        }

        composable(route = Screen.Browse.route) {
            // Placeholder for Browse Screen
            BoxWithText(text = "Browse Screen - Coming Soon")
        }

        composable(route = Screen.ProfileSetup.route) {
            // Placeholder for Profile Setup Screen
            BoxWithText(text = "Profile Setup - Coming Soon")
        }
    }
}

// Helper composable for placeholder screens
@Composable
fun BoxWithText(text: String) {
    androidx.compose.foundation.layout.Box(
        contentAlignment = androidx.compose.ui.Alignment.Center,
        modifier = androidx.compose.ui.Modifier.fillMaxSize()
    ) {
        androidx.compose.material3.Text(text = text)
    }
}