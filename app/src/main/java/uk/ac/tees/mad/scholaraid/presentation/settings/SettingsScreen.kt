package uk.ac.tees.mad.scholaraid.presentation.settings

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import uk.ac.tees.mad.scholaraid.presentation.profile_setup.ProfileSetupEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onLogout: () -> Unit,
    navController: NavController,
    viewModel: SettingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var notificationsEnabled by remember { mutableStateOf(true) }

    // Observe for success/error messages
    LaunchedEffect(state.isSuccess, state.errorMessage) {
        if (state.isSuccess) {
            Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
            // Reset success state after showing toast
            viewModel.onEvent(ProfileSetupEvent.ClearError)
        }
        if (state.errorMessage != null) {
            Toast.makeText(context, state.errorMessage, Toast.LENGTH_LONG).show()
            viewModel.onEvent(ProfileSetupEvent.ClearError)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // --- Profile Section (Editable) ---
            Text(
                text = "Account and Profile",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Gravatar Profile Photo
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape), // Use CircleShape for a modern profile look
                        contentAlignment = Alignment.Center
                    ) {
                        SubcomposeAsyncImage(
                            model = getGravatarUrl(state.userEmail),
                            contentDescription = "Profile Photo from Email",
                            loading = {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(30.dp),
                                    strokeWidth = 2.dp
                                )
                            },
                            error = {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Default Profile Image",
                                    modifier = Modifier.size(100.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Email Display (Read-only)
                    OutlinedTextField(
                        value = state.userEmail,
                        onValueChange = { /* Read-only */ },
                        label = { Text("Email") },
                        leadingIcon = { Icon(Icons.Default.MailOutline, contentDescription = "Email") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false,
                        readOnly = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Full Name
                    OutlinedTextField(
                        value = state.fullName,
                        onValueChange = { viewModel.onEvent(ProfileSetupEvent.FullNameChanged(it)) },
                        label = { Text("Full Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Full Name") },
                        placeholder = { Text("Enter your full name") },
                        isError = state.fullNameError != null,
                        supportingText = { state.fullNameError?.let { Text(it) } },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Academic Level
                    OutlinedTextField(
                        value = state.academicLevel,
                        onValueChange = { viewModel.onEvent(ProfileSetupEvent.AcademicLevelChanged(it)) },
                        label = { Text("Academic Level") },
                        leadingIcon = { Icon(Icons.Default.Info, contentDescription = "Academic Level") },
                        placeholder = { Text("e.g. Undergraduate, Masters") },
                        isError = state.academicLevelError != null,
                        supportingText = { state.academicLevelError?.let { Text(it) } },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Field of Study
                    OutlinedTextField(
                        value = state.fieldOfStudy,
                        onValueChange = { viewModel.onEvent(ProfileSetupEvent.FieldOfStudyChanged(it)) },
                        label = { Text("Field of Study") },
                        leadingIcon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Field of Study") },
                        placeholder = { Text("e.g. Computer Science") },
                        isError = state.fieldOfStudyError != null,
                        supportingText = { state.fieldOfStudyError?.let { Text(it) } },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // GPA and University (Optional fields)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = state.gpa,
                            onValueChange = { viewModel.onEvent(ProfileSetupEvent.GpaChanged(it)) },
                            label = { Text("GPA (Optional)") },
                            leadingIcon = { Icon(Icons.Default.Face, contentDescription = "GPA") },
                            placeholder = { Text("e.g. 3.5") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = state.university,
                            onValueChange = { viewModel.onEvent(ProfileSetupEvent.UniversityChanged(it)) },
                            label = { Text("University (Optional)") },
                            leadingIcon = { Icon(Icons.Default.Info, contentDescription = "University") },
                            placeholder = { Text("Enter university") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Save Profile Button
                    Button(
                        onClick = { viewModel.onEvent(ProfileSetupEvent.SaveProfile) },
                        enabled = !state.isLoading,
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = "Save")
                                Text(
                                    text = "Save Profile Changes",
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Application Settings Section ---
            Text(
                text = "Application Settings",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Notification Preferences
                    ListItem(
                        headlineContent = { Text("Notification Preferences") },
                        supportingContent = { Text("Manage email and push notifications") },
                        leadingContent = {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingContent = {
                            Switch(
                                checked = notificationsEnabled, // Use the state variable
                                onCheckedChange = {
                                    notificationsEnabled = it // Update the state when toggled
                                    Toast.makeText(context, "Notifications set to: ${if (it) "ON" else "OFF"}", Toast.LENGTH_SHORT).show()
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Logout Option ---
            Card(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = "Log Out",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Generates a Gravatar URL from the user's email address
 * ... (No change to this function)
 */
private fun getGravatarUrl(email: String, size: Int = 200, default: String = "mp"): String {
    if (email.isBlank()) {
        return "" // Return empty string if no email
    }

    // Generate MD5 hash of the email (trimmed and lowercased)
    val emailHash = md5(email.trim().lowercase())

    // Construct Gravatar URL
    return "https://www.gravatar.com/avatar/$emailHash?s=$size&d=$default"
}

/**
 * Simple MD5 hash function for Gravatar
 * ... (No change to this function)
 */
private fun md5(input: String): String {
    val md = java.security.MessageDigest.getInstance("MD5")
    val digested = md.digest(input.toByteArray())
    return digested.joinToString("") { "%02x".format(it) }
}