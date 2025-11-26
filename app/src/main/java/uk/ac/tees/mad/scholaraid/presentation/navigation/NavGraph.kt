package uk.ac.tees.mad.scholaraid.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

        // Browse Screen (Main screen after login)
        composable(route = Screen.Browse.route) {
            BoxWithText(text = "Browse Screen - Coming Soon!\nScholarships will be displayed here.")
        }

        // Detail Screen
        composable(route = Screen.Detail.route) {
            BoxWithText(text = "Detail Screen - Coming Soon!")
        }

        // Saved Screen
        composable(route = Screen.Saved.route) {
            BoxWithText(text = "Saved Scholarships - Coming Soon!")
        }

        // Settings Screen
        composable(route = Screen.Settings.route) {
            BoxWithText(text = "Settings - Coming Soon!")
        }
    }
}

// Helper composable for placeholder screens
@Composable
fun BoxWithText(text: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = text)
    }
}