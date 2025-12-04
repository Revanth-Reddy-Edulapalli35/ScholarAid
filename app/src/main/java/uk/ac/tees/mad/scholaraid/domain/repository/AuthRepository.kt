package uk.ac.tees.mad.scholaraid.domain.repository

import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.scholaraid.util.Resource
import uk.ac.tees.mad.scholaraid.data.model.AuthResult

interface AuthRepository {
    fun loginUser(email: String, password: String): Flow<AuthResult>
    fun registerUser(email: String, password: String): Flow<AuthResult>
    fun getCurrentUser(): Flow<Boolean>
    fun logout(): Flow<Resource<Boolean>>
    fun resetPassword(email: String): Flow<AuthResult>
}