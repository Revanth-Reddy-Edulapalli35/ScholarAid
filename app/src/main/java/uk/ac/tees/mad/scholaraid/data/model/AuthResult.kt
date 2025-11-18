package uk.ac.tees.mad.scholaraid.data.model

sealed class AuthResult {
    data class Success(val userId: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
    object Loading : AuthResult()
}