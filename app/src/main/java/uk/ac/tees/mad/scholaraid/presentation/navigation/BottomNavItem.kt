package uk.ac.tees.mad.scholaraid.presentation.navigation


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Data class representing bottom navigation items
 */
data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

/**
 * List of bottom navigation items
 */
val bottomNavItems = listOf(
    BottomNavItem(
        label = "Home",
        icon = Icons.Default.Home,
        route = Screen.Scholarship.route
    ),
    BottomNavItem(
        label = "Saved",
        icon = Icons.Default.Favorite,
        route = Screen.Saved.route
    ),
    BottomNavItem(
        label = "Settings",
        icon = Icons.Default.Settings,
        route = Screen.Settings.route
    )
)