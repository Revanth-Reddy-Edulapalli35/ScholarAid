package uk.ac.tees.mad.scholaraid.presentation.profile_setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uk.ac.tees.mad.scholaraid.domain.model.UserProfile
import uk.ac.tees.mad.scholaraid.domain.repository.UserRepository
import uk.ac.tees.mad.scholaraid.util.Resource
import javax.inject.Inject

@HiltViewModel
class ProfileSetupViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileSetupState())
    val state = _state.asStateFlow()

    fun onEvent(event: ProfileSetupEvent) {
        when (event) {
            is ProfileSetupEvent.FullNameChanged -> {
                _state.update { it.copy(
                    fullName = event.fullName,
                    fullNameError = null
                ) }
            }
            is ProfileSetupEvent.AcademicLevelChanged -> {
                _state.update { it.copy(
                    academicLevel = event.academicLevel,
                    academicLevelError = null
                ) }
            }
            is ProfileSetupEvent.FieldOfStudyChanged -> {
                _state.update { it.copy(
                    fieldOfStudy = event.fieldOfStudy,
                    fieldOfStudyError = null
                ) }
            }
            is ProfileSetupEvent.GpaChanged -> {
                _state.update { it.copy(gpa = event.gpa) }
            }
            is ProfileSetupEvent.UniversityChanged -> {
                _state.update { it.copy(university = event.university) }
            }
            is ProfileSetupEvent.ProfileImageSelected -> {
                _state.update { it.copy(
                    profileImageBytes = event.imageBytes,
                    profileImageUri = event.imageUri
                ) }
            }
            ProfileSetupEvent.TakePhoto -> {
                // Handled in UI layer
            }
            ProfileSetupEvent.SelectFromGallery -> {
                // Handled in UI layer
            }
            ProfileSetupEvent.SaveProfile -> {
                saveProfile()
            }
            ProfileSetupEvent.ClearError -> {
                _state.update { it.copy(errorMessage = null) }
            }
        }
    }

    private fun saveProfile() {
        val fullName = _state.value.fullName
        val academicLevel = _state.value.academicLevel
        val fieldOfStudy = _state.value.fieldOfStudy

        // Basic validation
        val fullNameError = if (fullName.isBlank()) "Full name is required" else null
        val academicLevelError = if (academicLevel.isBlank()) "Academic level is required" else null
        val fieldOfStudyError = if (fieldOfStudy.isBlank()) "Field of study is required" else null

        if (fullNameError != null || academicLevelError != null || fieldOfStudyError != null) {
            _state.update { it.copy(
                fullNameError = fullNameError,
                academicLevelError = academicLevelError,
                fieldOfStudyError = fieldOfStudyError
            ) }
            return
        }

        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            _state.update { it.copy(
                errorMessage = "User not authenticated"
            ) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                var profilePhotoUrl = ""

                // Upload profile image if selected
                _state.value.profileImageBytes?.let { imageBytes ->
                    userRepository.uploadProfileImage(currentUser.uid, imageBytes).collect { result ->
                        when (result) {
                            is Resource.Success -> {
                                profilePhotoUrl = result.data ?: ""
                            }
                            is Resource.Error -> {
                                _state.update { it.copy(
                                    isLoading = false,
                                    errorMessage = result.message ?: "Failed to upload image"
                                ) }
                                return@collect
                            }
                            is Resource.Loading -> { }
                        }
                    }
                }

                // Create user profile
                val userProfile = UserProfile(
                    userId = currentUser.uid,
                    email = currentUser.email ?: "",
                    fullName = fullName,
                    profilePhotoUrl = profilePhotoUrl,
                    academicLevel = academicLevel,
                    fieldOfStudy = fieldOfStudy,
                    gpa = _state.value.gpa,
                    university = _state.value.university
                )

                // Save profile to Firestore
                userRepository.saveUserProfile(userProfile).collect { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _state.update { it.copy(isLoading = true) }
                        }
                        is Resource.Success -> {
                            _state.update { it.copy(
                                isLoading = false,
                                isSuccess = true,
                                errorMessage = null
                            ) }
                        }
                        is Resource.Error -> {
                            _state.update { it.copy(
                                isLoading = false,
                                errorMessage = result.message ?: "Failed to save profile"
                            ) }
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Failed to save profile"
                ) }
            }
        }
    }
}