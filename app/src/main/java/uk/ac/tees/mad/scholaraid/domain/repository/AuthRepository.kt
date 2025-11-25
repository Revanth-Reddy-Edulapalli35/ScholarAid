package uk.ac.tees.mad.scholaraid.domain.repository

import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.scholaraid.util.Resource

interface AuthRepository {
    fun loginUser(email: String, password: String): Flow<Resource<AuthResult>>
    fun registerUser(email: String, password: String): Flow<Resource<AuthResult>>
    fun getCurrentUser(): Flow<Boolean>
    fun logout(): Flow<Resource<Boolean>>
}