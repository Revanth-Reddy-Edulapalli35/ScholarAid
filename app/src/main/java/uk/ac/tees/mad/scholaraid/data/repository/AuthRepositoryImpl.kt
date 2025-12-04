package uk.ac.tees.mad.scholaraid.data.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import uk.ac.tees.mad.scholaraid.domain.repository.AuthRepository
import uk.ac.tees.mad.scholaraid.util.Resource
import javax.inject.Inject
import uk.ac.tees.mad.scholaraid.data.model.AuthResult

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override fun loginUser(email: String, password: String): Flow<AuthResult> = flow {
        try {
            emit(AuthResult.Loading)
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: throw Exception("User ID is null")
            emit(AuthResult.Success(userId))
        } catch (e: Exception) {
            emit(AuthResult.Error(e.message ?: "Login failed"))
        }
    }

    override fun registerUser(email: String, password: String): Flow<AuthResult> = flow {
        try {
            emit(AuthResult.Loading)
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: throw Exception("User ID is null")
            emit(AuthResult.Success(userId))
        } catch (e: Exception) {
            emit(AuthResult.Error(e.message ?: "Sign up failed"))
        }
    }

    override fun getCurrentUser(): Flow<Boolean> = flow {
        val currentUser = firebaseAuth.currentUser
        emit(currentUser != null)
    }

    override fun logout(): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            firebaseAuth.signOut()
            emit(Resource.Success(true))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Logout failed"))
        }
    }

    override fun resetPassword(email: String): Flow<AuthResult> = flow {
        try {
            emit(AuthResult.Loading)
            firebaseAuth.sendPasswordResetEmail(email).await()
            emit(AuthResult.Success("Password reset email sent"))
        } catch (e: Exception) {
            emit(AuthResult.Error(e.message ?: "Password reset failed"))
        }
    }
}