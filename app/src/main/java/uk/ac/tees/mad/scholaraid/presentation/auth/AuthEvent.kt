package uk.ac.tees.mad.scholaraid.presentation.auth

sealed class AuthEvent {
    data class EmailChanged(val email: String) : AuthEvent()
    data class PasswordChanged(val password: String) : AuthEvent()
    data class ConfirmPasswordChanged(val confirmPassword: String) : AuthEvent()
    data class ResetPassword(val email: String) : AuthEvent()
    object ToggleAuthMode : AuthEvent()
    object Login : AuthEvent()
    object Register : AuthEvent()
    object ClearError : AuthEvent()
}