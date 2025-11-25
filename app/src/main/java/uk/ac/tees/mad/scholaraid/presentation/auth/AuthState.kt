package uk.ac.tees.mad.scholaraid.presentation.auth

data class AuthState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,

    // Form fields
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",

    // Form validation
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,

    // Screen state
    val isLoginMode: Boolean = true
)