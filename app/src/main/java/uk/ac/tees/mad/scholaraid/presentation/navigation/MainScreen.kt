package uk.ac.tees.mad.scholaraid.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import uk.ac.tees.mad.scholaraid.presentation.detail.ScholarshipDetailScreen
import uk.ac.tees.mad.scholaraid.presentation.save.SaveScreen
import uk.ac.tees.mad.scholaraid.presentation.scholarship.ScholarshipListScreen
import uk.ac.tees.mad.scholaraid.presentation.settings.SettingsScreen

@Composable
fun MainScreen(mainNavController: NavHostController) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp
            ) {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label
                            )
                        },
                        label = { Text(item.label) },
                        selected = currentRoute?.contains(item.route) == true, // Check if current route contains base route
                        onClick = {
                            bottomNavController.navigate(item.route) {
                                // Avoid building up a large stack of destinations on the back stack as users select items
                                popUpTo(bottomNavController.graph.startDestinationId) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = bottomNavController,
            startDestination = Screen.Scholarship.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Scholarship.route) {
                ScholarshipListScreen(navController = bottomNavController)
            }

            composable(Screen.Saved.route) {
                SaveScreen(navController = bottomNavController)
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    navController = bottomNavController,
                    onLogout = {
                        FirebaseAuth.getInstance().signOut()
                        mainNavController.navigate(Screen.Auth.route) {
                            popUpTo(Screen.Main.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = Screen.ScholarshipDetail.route,
                arguments = listOf(
                    navArgument("scholarshipId") {
                        type = androidx.navigation.NavType.StringType
                    }
                )
            ) { backStackEntry ->
                // The ScholarshipDetailScreen is defined here to receive the scholarshipId
                ScholarshipDetailScreen(
                    navController = bottomNavController,
                    viewModel = hiltViewModel()
                )
            }
        }
    }
}