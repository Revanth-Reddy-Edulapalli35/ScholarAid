package uk.ac.tees.mad.scholaraid.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash_screen")
    object Auth : Screen("auth_screen")
    object ProfileSetup : Screen("profile_setup_screen")
    object Main : Screen("main")

    // Bottom nav destinations
    object Scholarship : Screen("scholarship_screen")
    object Saved : Screen("saved_screen")
    object Settings : Screen("settings_screen")

    // Detail Screen
    object ScholarshipDetail : Screen("scholarship_detail/{scholarshipId}"){
        fun createRoute(scholarshipId: String) = "scholarship_detail/$scholarshipId"
    }
}