package uk.ac.tees.mad.scholaraid.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import uk.ac.tees.mad.scholaraid.domain.repository.AuthRepository
import uk.ac.tees.mad.scholaraid.util.Constants
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _navigationEvent = MutableStateFlow<String?>(null)
    val navigationEvent = _navigationEvent.asStateFlow()

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            // Simulate splash delay
            delay(Constants.SPLASH_DELAY)

            try {
                val currentUser = firebaseAuth.currentUser

                if (currentUser != null) {
                    // User is logged in, check if profile is completed
                    val hasProfile = checkProfileExists(currentUser.uid)

                    _navigationEvent.value = if (hasProfile) {
                        "browse_screen"
                    } else {
                        "profile_setup_screen"
                    }
                } else {
                    // User is not logged in
                    _navigationEvent.value = "auth_screen"
                }

                _isLoading.value = false
            } catch (e: Exception) {
                // On error, navigate to auth screen
                _navigationEvent.value = "auth_screen"
                _isLoading.value = false
            }
        }
    }

    private suspend fun checkProfileExists(userId: String): Boolean {
        return try {
            val document = firestore.collection(Constants.USERS_COLLECTION)
                .document(userId)
                .get()
                .await()

            document.exists()
        } catch (e: Exception) {
            false
        }
    }

    fun resetNavigationEvent() {
        _navigationEvent.value = null
    }
}