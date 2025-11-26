package uk.ac.tees.mad.scholaraid.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import uk.ac.tees.mad.scholaraid.domain.model.UserProfile
import uk.ac.tees.mad.scholaraid.domain.repository.UserRepository
import uk.ac.tees.mad.scholaraid.util.Constants
import uk.ac.tees.mad.scholaraid.util.Resource
import java.util.UUID
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : UserRepository {

    override fun saveUserProfile(userProfile: UserProfile): Flow<Resource<Boolean>> = flow {
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

    override fun uploadProfileImage(userId: String, imageBytes: ByteArray): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val fileName = "profile_${userId}_${UUID.randomUUID()}.jpg"
            val storageRef = storage.reference.child("profile_images/$fileName")

            val uploadTask = storageRef.putBytes(imageBytes).await()
            val downloadUrl = storageRef.downloadUrl.await()

            emit(Resource.Success(downloadUrl.toString()))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to upload image"))
        }
    }
}