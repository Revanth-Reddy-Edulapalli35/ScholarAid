package uk.ac.tees.mad.scholaraid.presentation.profile_setup

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.launch
import uk.ac.tees.mad.scholaraid.domain.model.UserProfile
import uk.ac.tees.mad.scholaraid.presentation.navigation.Screen
import uk.ac.tees.mad.scholaraid.util.ImageUtil
import uk.ac.tees.mad.scholaraid.util.PermissionUtil
import uk.ac.tees.mad.scholaraid.util.rememberCameraUtil

// Define your primary color here or import from theme
private val PrimaryBlue = Color(0xFF2196F3)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ProfileSetupScreen(
    navController: NavController,
    viewModel: ProfileSetupViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val scope = rememberCoroutineScope()
    val cameraUtil = rememberCameraUtil(context)
    val tempImageUri = remember { mutableStateOf<Uri?>(null) }
    val cameraPermissionsState = rememberMultiplePermissionsState(
        permissions = PermissionUtil.cameraPermissions
    )
    val galleryPermissions = PermissionUtil.galleryPermissions
    val galleryPermissionsState = rememberMultiplePermissionsState(
        permissions = galleryPermissions
    )

    fun processImageFromUri(uri: Uri, viewModel: ProfileSetupViewModel) {
        viewModel.onEvent(ProfileSetupEvent.ClearError)
        scope.launch {
            try {
                val compressedImage = ImageUtil.compressImage(context, uri, 800)
                compressedImage?.let { bytes ->
                    viewModel.onEvent(
                        ProfileSetupEvent.ProfileImageSelected(
                            imageBytes = bytes,
                            imageUri = uri.toString()
                        )
                    )
                } ?: run {
                    snackbarHostState.showSnackbar("Failed to process image")
                }
            } catch (e: Exception) {
                snackbarHostState.showSnackbar("Error processing image: ${e.message}")
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                tempImageUri.value?.let { uri ->
                    processImageFromUri(uri, viewModel)
                }
            }
        }
    )

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                processImageFromUri(it, viewModel)
            }
        }
    )

    LaunchedEffect(cameraPermissionsState.allPermissionsGranted) {
        if (cameraPermissionsState.allPermissionsGranted) {
            // Permissions granted
        } else if (cameraPermissionsState.shouldShowRationale) {
            scope.launch { snackbarHostState.showSnackbar("Camera permissions are required") }
        }
    }

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
                // Profile Photo Section
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            contentAlignment = Alignment.BottomEnd
                        ) {

                            // --- MODIFICATION START ---
                            // This Box handles the main image/icon
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .background(Color.LightGray), // Lighter gray
                                contentAlignment = Alignment.Center
                            ) {
                                if (state.profileImageUri != null) {
                                    Image(
                                        painter = rememberAsyncImagePainter(state.profileImageUri),
                                        contentDescription = "Profile Photo",
                                        modifier = Modifier.fillMaxSize(), // Fill the box
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle, // Safe, built-in icon
                                        contentDescription = "Profile Photo",
                                        modifier = Modifier.size(80.dp),
                                        tint = Color.Gray
                                    )
                                }
                            }
                            // --- MODIFICATION END ---

                            IconButton(
                                onClick = {
                                    if (cameraPermissionsState.allPermissionsGranted) {
                                        val file = cameraUtil.createImageFile()
                                        val uri = cameraUtil.getImageUri(file)
                                        tempImageUri.value = uri
                                        cameraLauncher.launch(uri)
                                    } else {
                                        cameraPermissionsState.launchMultiplePermissionRequest()
                                    }
                                },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(PrimaryBlue, CircleShape)
                            ) {
                                Icon(
                                    Icons.Default.AccountCircle, // Changed to a camera icon
                                    contentDescription = "Take Photo",
                                    tint = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (cameraPermissionsState.allPermissionsGranted) {
                                        val file = cameraUtil.createImageFile()
                                        val uri = cameraUtil.getImageUri(file)
                                        tempImageUri.value = uri
                                        cameraLauncher.launch(uri)
                                    } else {
                                        cameraPermissionsState.launchMultiplePermissionRequest()
                                    }
                                }
                            ) {
                                Text("Take Photo")
                            }

                            Button(
                                onClick = {
                                    if (galleryPermissions.isEmpty() || galleryPermissionsState.allPermissionsGranted) {
                                        galleryLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    } else {
                                        galleryPermissionsState.launchMultiplePermissionRequest()
                                    }
                                }
                            ) {
                                Text("Choose from Gallery")
                            }
                        }

                        // Permission rationale
                        if (cameraPermissionsState.shouldShowRationale) {
                            Text(
                                text = "Camera permission is required to take photos",
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        if (galleryPermissionsState.shouldShowRationale) {
                            Text(
                                text = "Storage permission is required to select photos",
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }

                // Personal Information
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
                            color = PrimaryBlue
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
                                UserProfile.ACADEMIC_LEVELS.forEach { level ->
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
                                UserProfile.FIELDS_OF_STUDY.forEach { field ->
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
                                    ProfileSetupEvent.UniversityChanged(
                                        it
                                    )
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