package uk.ac.tees.mad.scholaraid.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash_screen")
    object Auth : Screen("auth_screen")
    object ProfileSetup : Screen("profile_setup_screen")
    object Browse : Screen("browse_screen")
    object Detail : Screen("detail_screen")
    object Saved : Screen("saved_screen")
    object Settings : Screen("settings_screen")
}