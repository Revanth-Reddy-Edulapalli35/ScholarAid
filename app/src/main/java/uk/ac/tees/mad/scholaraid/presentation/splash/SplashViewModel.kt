package uk.ac.tees.mad.scholaraid.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.ac.tees.mad.scholaraid.domain.repository.AuthRepository
import uk.ac.tees.mad.scholaraid.util.Constants
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
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

            authRepository.getCurrentUser().collect { isLoggedIn ->
                _navigationEvent.value = if (isLoggedIn) {
                    "browse_screen"
                } else {
                    "auth_screen"
                }
                _isLoading.value = false
            }
        }
    }

    fun resetNavigationEvent() {
        _navigationEvent.value = null
    }
}
