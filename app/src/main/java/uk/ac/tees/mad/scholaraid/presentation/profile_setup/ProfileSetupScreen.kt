package uk.ac.tees.mad.scholaraid.presentation.profile_setup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import uk.ac.tees.mad.scholaraid.presentation.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
    navController: NavController,
    viewModel: ProfileSetupViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = state.isSuccess) {
        if (state.isSuccess) {
            navController.navigate(Screen.Main.route) {
                popUpTo(Screen.ProfileSetup.route) { inclusive = true }
                popUpTo(Screen.Auth.route) { inclusive = true }
            }
        }
    }

    LaunchedEffect(key1 = state.errorMessage) {
        state.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.onEvent(ProfileSetupEvent.ClearError)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Complete Your Profile") }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Profile Photo Section - Simplified with default icon only
//                Card(
//                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//                ) {
//                    Column(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(16.dp),
//                        horizontalAlignment = Alignment.CenterHorizontally
//                    ) {
//                        // Default Profile Icon
//                        Icon(
//                            imageVector = Icons.Default.AccountCircle,
//                            contentDescription = "Default Profile Photo",
//                            modifier = Modifier.size(120.dp),
//                            tint = Color.Gray
//                        )
//
//                        Spacer(modifier = Modifier.height(16.dp))
//
//                        Text(
//                            text = "Default profile photo",
//                            style = MaterialTheme.typography.bodyMedium,
//                            color = MaterialTheme.colorScheme.onSurfaceVariant
//                        )
//                    }
//                }

                // Personal Information Card (unchanged)
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Personal Information",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        // Full Name
                        OutlinedTextField(
                            value = state.fullName,
                            onValueChange = { viewModel.onEvent(ProfileSetupEvent.FullNameChanged(it)) },
                            label = { Text("Full Name") },
                            placeholder = { Text("Enter your full name") },
                            isError = state.fullNameError != null,
                            supportingText = {
                                state.fullNameError?.let { error ->
                                    Text(text = error, color = Color.Red)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Academic Level Dropdown
                        var academicLevelExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = academicLevelExpanded,
                            onExpandedChange = { academicLevelExpanded = !academicLevelExpanded }
                        ) {
                            OutlinedTextField(
                                value = state.academicLevel,
                                onValueChange = { },
                                label = { Text("Academic Level") },
                                placeholder = { Text("Select your academic level") },
                                readOnly = true,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = academicLevelExpanded
                                    )
                                },
                                isError = state.academicLevelError != null,
                                supportingText = {
                                    state.academicLevelError?.let { error ->
                                        Text(text = error, color = Color.Red)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )

                            ExposedDropdownMenu(
                                expanded = academicLevelExpanded,
                                onDismissRequest = { academicLevelExpanded = false }
                            ) {
                                uk.ac.tees.mad.scholaraid.domain.model.UserProfile.ACADEMIC_LEVELS.forEach { level ->
                                    DropdownMenuItem(
                                        text = { Text(level) },
                                        onClick = {
                                            viewModel.onEvent(
                                                ProfileSetupEvent.AcademicLevelChanged(level)
                                            )
                                            academicLevelExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Field of Study Dropdown
                        var fieldOfStudyExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = fieldOfStudyExpanded,
                            onExpandedChange = { fieldOfStudyExpanded = !fieldOfStudyExpanded }
                        ) {
                            OutlinedTextField(
                                value = state.fieldOfStudy,
                                onValueChange = { },
                                label = { Text("Field of Study") },
                                placeholder = { Text("Select your field of study") },
                                readOnly = true,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = fieldOfStudyExpanded
                                    )
                                },
                                isError = state.fieldOfStudyError != null,
                                supportingText = {
                                    state.fieldOfStudyError?.let { error ->
                                        Text(text = error, color = Color.Red)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )

                            ExposedDropdownMenu(
                                expanded = fieldOfStudyExpanded,
                                onDismissRequest = { fieldOfStudyExpanded = false }
                            ) {
                                uk.ac.tees.mad.scholaraid.domain.model.UserProfile.FIELDS_OF_STUDY.forEach { field ->
                                    DropdownMenuItem(
                                        text = { Text(field) },
                                        onClick = {
                                            viewModel.onEvent(
                                                ProfileSetupEvent.FieldOfStudyChanged(field)
                                            )
                                            fieldOfStudyExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // GPA
                        OutlinedTextField(
                            value = state.gpa,
                            onValueChange = { viewModel.onEvent(ProfileSetupEvent.GpaChanged(it)) },
                            label = { Text("GPA (Optional)") },
                            placeholder = { Text("Enter your GPA") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // University
                        OutlinedTextField(
                            value = state.university,
                            onValueChange = {
                                viewModel.onEvent(
                                    ProfileSetupEvent.UniversityChanged(it)
                                )
                            },
                            label = { Text("University (Optional)") },
                            placeholder = { Text("Enter your university") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Save Button
                Button(
                    onClick = { viewModel.onEvent(ProfileSetupEvent.SaveProfile) },
                    enabled = !state.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = "Complete Profile",
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}