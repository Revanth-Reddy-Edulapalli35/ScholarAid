package uk.ac.tees.mad.scholaraid.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uk.ac.tees.mad.scholaraid.domain.repository.AuthRepository
import uk.ac.tees.mad.scholaraid.util.Resource
import uk.ac.tees.mad.scholaraid.util.ValidationUtil
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.EmailChanged -> {
                _state.update { it.copy(email = event.email) }
            }
            is AuthEvent.PasswordChanged -> {
                _state.update { it.copy(password = event.password) }
            }
            is AuthEvent.ConfirmPasswordChanged -> {
                _state.update { it.copy(confirmPassword = event.confirmPassword) }
            }
            AuthEvent.ToggleAuthMode -> {
                _state.update { it.copy(
                    isLoginMode = !it.isLoginMode,
                    emailError = null,
                    passwordError = null,
                    confirmPasswordError = null,
                    errorMessage = null
                ) }
            }
            AuthEvent.Login -> {
                login()
            }
            AuthEvent.Register -> {
                register()
            }
            AuthEvent.ClearError -> {
                _state.update { it.copy(errorMessage = null) }
            }
        }
    }

    private fun login() {
        val email = _state.value.email
        val password = _state.value.password

        val emailError = ValidationUtil.validateEmail(email)
        val passwordError = ValidationUtil.validatePassword(password)

        if (emailError != null || passwordError != null) {
            _state.update { it.copy(
                emailError = emailError,
                passwordError = passwordError
            ) }
            return
        }

        viewModelScope.launch {
            authRepository.loginUser(email, password).collect { result ->
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
                            errorMessage = result.message
                        ) }
                    }
                }
            }
        }
    }

    private fun register() {
        val email = _state.value.email
        val password = _state.value.password
        val confirmPassword = _state.value.confirmPassword

        val emailError = ValidationUtil.validateEmail(email)
        val passwordError = ValidationUtil.validatePassword(password)
        val confirmPasswordError = ValidationUtil.validateConfirmPassword(password, confirmPassword)

        if (emailError != null || passwordError != null || confirmPasswordError != null) {
            _state.update { it.copy(
                emailError = emailError,
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError
            ) }
            return
        }

        viewModelScope.launch {
            authRepository.registerUser(email, password).collect { result ->
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
                            errorMessage = result.message
                        ) }
                    }
                }
            }
        }
    }
}