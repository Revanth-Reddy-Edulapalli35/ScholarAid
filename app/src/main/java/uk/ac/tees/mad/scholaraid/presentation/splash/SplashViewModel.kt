package uk.ac.tees.mad.scholaraid.presentation.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import uk.ac.tees.mad.scholaraid.util.Constants
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val TAG = "SplashViewModel"

    private val _navigationEvent = MutableStateFlow<String?>(null)
    val navigationEvent = _navigationEvent.asStateFlow()

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            try {
                val currentUser = firebaseAuth.currentUser

                if (currentUser == null) {
                    Log.d(TAG, "checkAuthStatus: No user logged in, navigating to Auth")
                    _navigationEvent.value = "auth_screen"
                } else {
                    Log.d(TAG, "checkAuthStatus: User logged in: ${currentUser.uid}")

                    // Check if user has completed profile setup
                    val hasProfile = checkUserProfile(currentUser.uid)

                    if (hasProfile) {
                        Log.d(TAG, "checkAuthStatus: User has profile, navigating to Main")
                        _navigationEvent.value = "main"
                    } else {
                        Log.d(TAG, "checkAuthStatus: User missing profile, navigating to ProfileSetup")
                        _navigationEvent.value = "profile_setup_screen"
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "checkAuthStatus: Error checking auth status", e)
                // On error, default to auth screen
                _navigationEvent.value = "auth_screen"
            }
        }
    }

    private suspend fun checkUserProfile(userId: String): Boolean {
        return try {
            val document = firestore.collection(Constants.USERS_COLLECTION)
                .document(userId)
                .get()
                .await()

            val exists = document.exists()
            Log.d(TAG, "checkUserProfile: Profile exists for user: $exists")
            exists
        } catch (e: Exception) {
            Log.e(TAG, "checkUserProfile: Error checking profile", e)
            false
        }
    }

    fun resetNavigationEvent() {
        _navigationEvent.value = null
    }
}