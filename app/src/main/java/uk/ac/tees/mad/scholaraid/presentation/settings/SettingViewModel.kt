package uk.ac.tees.mad.scholaraid.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uk.ac.tees.mad.scholaraid.domain.model.UserProfile
import uk.ac.tees.mad.scholaraid.domain.repository.SupabaseImageRepository
import uk.ac.tees.mad.scholaraid.domain.repository.UserRepository
import uk.ac.tees.mad.scholaraid.presentation.profile_setup.ProfileSetupEvent
import uk.ac.tees.mad.scholaraid.presentation.profile_setup.ProfileSetupState
import uk.ac.tees.mad.scholaraid.util.Resource
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val imageRepository: SupabaseImageRepository, // Inject image repository
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileSetupState())
    val state: StateFlow<ProfileSetupState> = _state.asStateFlow()

    private val currentUser = firebaseAuth.currentUser
        ?: throw IllegalStateException("User not logged in or Firebase Auth not initialized.")
    private val userId = currentUser.uid

    init {
        fetchUserProfile()
    }

    private fun fetchUserProfile() {
        viewModelScope.launch {
            userRepository.getUserProfile(userId).collectLatest { result ->
                when (result) {
                    is Resource.Loading -> _state.update { it.copy(isLoading = true) }
                    is Resource.Success -> {
                        val profile = result.data
                        if (profile != null) {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    fullName = profile.fullName,
                                    academicLevel = profile.academicLevel,
                                    fieldOfStudy = profile.fieldOfStudy,
                                    gpa = profile.gpa,
                                    university = profile.university,
                                    profileImageUri = profile.profilePhotoUrl
                                )
                            }
                        } else {
                            _state.update { it.copy(isLoading = false, errorMessage = "Profile data not found.") }
                        }
                    }
                    is Resource.Error -> _state.update { it.copy(isLoading = false, errorMessage = result.message) }
                }
            }
        }
    }

    fun onEvent(event: ProfileSetupEvent) {
        when (event) {
            is ProfileSetupEvent.FullNameChanged -> _state.update { it.copy(fullName = event.fullName, fullNameError = null) }
            is ProfileSetupEvent.AcademicLevelChanged -> _state.update { it.copy(academicLevel = event.academicLevel, academicLevelError = null) }
            is ProfileSetupEvent.FieldOfStudyChanged -> _state.update { it.copy(fieldOfStudy = event.fieldOfStudy, fieldOfStudyError = null) }
            is ProfileSetupEvent.GpaChanged -> _state.update { it.copy(gpa = event.gpa) }
            is ProfileSetupEvent.UniversityChanged -> _state.update { it.copy(university = event.university) }
            is ProfileSetupEvent.ProfileImageSelected -> _state.update {
                it.copy(
                    profileImageBytes = event.imageBytes,
                    profileImageUri = event.imageUri // Local URI for immediate preview
                )
            }
            ProfileSetupEvent.SaveProfile -> saveUserProfile()
            ProfileSetupEvent.ClearError -> _state.update { it.copy(errorMessage = null) }
        }
    }

    private fun saveUserProfile() {
        if (!validateForm()) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            // 1. Handle image upload if a new one was selected
            val imageUploadFlow = _state.value.profileImageBytes?.let { bytes ->
                imageRepository.uploadProfileImage(userId, bytes)
            }

            if (imageUploadFlow != null) {
                imageUploadFlow.collectLatest { resource ->
                    when (resource) {
                        is Resource.Loading -> _state.update { it.copy(isLoading = true) }
                        is Resource.Success -> updateProfileData(resource.data)
                        is Resource.Error -> _state.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = resource.message ?: "Image upload failed."
                            )
                        }
                    }
                }
            } else {
                // 2. If no new image, update profile with existing URL
                updateProfileData(_state.value.profileImageUri)
            }
        }
    }

    private suspend fun updateProfileData(profilePhotoUrl: String?) {
        val userProfile = UserProfile(
            userId = userId,
            email = currentUser.email ?: "",
            fullName = _state.value.fullName,
            profilePhotoUrl = profilePhotoUrl ?: "",
            academicLevel = _state.value.academicLevel,
            fieldOfStudy = _state.value.fieldOfStudy,
            gpa = _state.value.gpa,
            university = _state.value.university
        )

        val result = userRepository.updateUserProfile(userProfile)
        _state.update {
            when (result) {
                is Resource.Success -> it.copy(
                    isLoading = false,
                    isSuccess = true, // Use isSuccess for navigation/snackbar
                    errorMessage = null,
                    profileImageBytes = null // Clear bytes after successful upload
                )
                is Resource.Error -> it.copy(
                    isLoading = false,
                    errorMessage = result.message ?: "Failed to update profile."
                )
                else -> it.copy(isLoading = false) // Should not happen for suspend fun
            }
        }
    }

    private fun validateForm(): Boolean {
        // Validation logic, same as in ProfileSetupViewModel
        val current = _state.value
        var isValid = true
        if (current.fullName.isBlank()) {
            _state.update { it.copy(fullNameError = "Full name is required") }
            isValid = false
        }
        if (current.academicLevel.isBlank()) {
            _state.update { it.copy(academicLevelError = "Academic level is required") }
            isValid = false
        }
        if (current.fieldOfStudy.isBlank()) {
            _state.update { it.copy(fieldOfStudyError = "Field of study is required") }
            isValid = false
        }
        return isValid
    }
}