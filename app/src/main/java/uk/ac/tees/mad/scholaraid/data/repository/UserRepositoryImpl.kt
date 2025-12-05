package uk.ac.tees.mad.scholaraid.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import uk.ac.tees.mad.scholaraid.domain.model.UserProfile
import uk.ac.tees.mad.scholaraid.domain.repository.UserRepository
import uk.ac.tees.mad.scholaraid.util.Constants
import uk.ac.tees.mad.scholaraid.util.Resource
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserRepository {

    override suspend fun saveUserProfile(userProfile: UserProfile): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            firestore.collection(Constants.USERS_COLLECTION)
                .document(userProfile.userId)
                .set(userProfile)
                .await()
            emit(Resource.Success(true))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to save profile"))
        }
    }

    override fun getUserProfile(userId: String): Flow<Resource<UserProfile>> = flow {
        emit(Resource.Loading())
        try {
            val document = firestore.collection(Constants.USERS_COLLECTION)
                .document(userId)
                .get()
                .await()

            if (document.exists()) {
                val userProfile = document.toObject(UserProfile::class.java)
                emit(Resource.Success(userProfile!!))
            } else {
                emit(Resource.Error("Profile not found"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to get profile"))
        }
    }

    // Removed uploadProfileImage function

    override suspend fun updateUserProfile(userProfile: UserProfile): Resource<Boolean> {
        return try {
            firestore.collection(Constants.USERS_COLLECTION).document(userProfile.userId)
                .set(userProfile) // Use set() to fully replace/update the document
                .await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to update user profile")
        }
    }

    // --- Scholarship Functions ---

    override fun getSavedScholarshipIds(userId: String): Flow<Resource<List<String>>> = flow {
        emit(Resource.Loading())
        try {
            val userDocRef = firestore.collection(Constants.USERS_COLLECTION).document(userId)
            userDocRef.snapshots().map { snapshot ->
                if (snapshot.exists()) {
                    val profile = snapshot.toObject(UserProfile::class.java)
                    Resource.Success(profile?.savedScholarshipIds ?: emptyList())
                } else {
                    Resource.Error("User profile not found")
                }
            }.collect { emit(it) }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to get saved scholarships"))
        }
    }

    override suspend fun saveScholarship(userId: String, scholarshipId: String): Resource<Boolean> {
        return try {
            firestore.collection(Constants.USERS_COLLECTION).document(userId)
                .update("savedScholarshipIds", FieldValue.arrayUnion(scholarshipId))
                .await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to save scholarship")
        }
    }

    override suspend fun unsaveScholarship(userId: String, scholarshipId: String): Resource<Boolean> {
        return try {
            firestore.collection(Constants.USERS_COLLECTION).document(userId)
                .update("savedScholarshipIds", FieldValue.arrayRemove(scholarshipId))
                .await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to unsave scholarship")
        }
    }
}