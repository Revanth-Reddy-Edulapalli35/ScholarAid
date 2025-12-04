package uk.ac.tees.mad.scholaraid.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uk.ac.tees.mad.scholaraid.data.model.AuthResult
import uk.ac.tees.mad.scholaraid.domain.repository.AuthRepository
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
                _state.update {
                    it.copy(
                        email = event.email,
                        emailError = null
                    )
                }
            }

            is AuthEvent.PasswordChanged -> {
                _state.update {
                    it.copy(
                        password = event.password,
                        passwordError = null
                    )
                }
            }

            is AuthEvent.ConfirmPasswordChanged -> {
                _state.update {
                    it.copy(
                        confirmPassword = event.confirmPassword,
                        confirmPasswordError = null
                    )
                }
            }

            AuthEvent.ToggleAuthMode -> {
                _state.update {
                    it.copy(
                        isLoginMode = !it.isLoginMode,
                        emailError = null,
                        passwordError = null,
                        confirmPasswordError = null,
                        errorMessage = null,
                        // Clear fields when switching modes
                        email = "",
                        password = "",
                        confirmPassword = ""
                    )
                }
            }

            AuthEvent.Login -> {
                login()
            }

            AuthEvent.Register -> {
                register()
            }

            is AuthEvent.ResetPassword -> {
                resetPassword(event.email)
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
            _state.update {
                it.copy(
                    emailError = emailError,
                    passwordError = passwordError
                )
            }
            return
        }

        viewModelScope.launch {
            authRepository.loginUser(email, password).collect { result ->
                when (result) {
                    is AuthResult.Loading -> {
                        _state.update {
                            it.copy(
                                isLoading = true,
                                errorMessage = null
                            )
                        }
                    }

                    is AuthResult.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                isSuccess = true,
                                isNewUser = false, // Login = existing user = Main screen
                                errorMessage = null
                            )
                        }
                    }

                    is AuthResult.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = formatErrorMessage(result.message)
                            )
                        }
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
            _state.update {
                it.copy(
                    emailError = emailError,
                    passwordError = passwordError,
                    confirmPasswordError = confirmPasswordError
                )
            }
            return
        }

        viewModelScope.launch {
            authRepository.registerUser(email, password).collect { result ->
                when (result) {
                    is AuthResult.Loading -> {
                        _state.update {
                            it.copy(
                                isLoading = true,
                                errorMessage = null
                            )
                        }
                    }

                    is AuthResult.Success -> {
                        // New user - always go to profile setup
                        _state.update {
                            it.copy(
                                isLoading = false,
                                isSuccess = true,
                                isNewUser = true, // Mark as new user
                                errorMessage = null
                            )
                        }
                    }

                    is AuthResult.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = formatErrorMessage(result.message)
                            )
                        }
                    }
                }
            }
        }
    }

    private fun resetPassword(email: String) {
        viewModelScope.launch {
            authRepository.resetPassword(email).collect { result ->
                when (result) {
                    is AuthResult.Loading -> {
                        _state.update {
                            it.copy(
                                isLoading = true,
                                errorMessage = null
                            )
                        }
                    }

                    is AuthResult.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "✓ Password reset email sent! Check your inbox."
                            )
                        }
                    }

                    is AuthResult.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = formatErrorMessage(result.message)
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Format Firebase error messages to be more user-friendly
     */
    private fun formatErrorMessage(message: String): String {
        return when {
            message.contains(
                "network",
                ignoreCase = true
            ) -> "Network error. Please check your connection."

            message.contains("password", ignoreCase = true) -> "Invalid email or password."
            message.contains("email", ignoreCase = true) -> "Invalid email format."
            message.contains(
                "user-not-found",
                ignoreCase = true
            ) -> "No account found with this email."

            message.contains("wrong-password", ignoreCase = true) -> "Incorrect password."
            message.contains(
                "email-already-in-use",
                ignoreCase = true
            ) -> "An account with this email already exists."

            message.contains(
                "weak-password",
                ignoreCase = true
            ) -> "Password should be at least 6 characters."

            else -> message
        }
    }
}