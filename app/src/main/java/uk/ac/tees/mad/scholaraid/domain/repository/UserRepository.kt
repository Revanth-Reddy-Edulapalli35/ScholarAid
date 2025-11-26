package uk.ac.tees.mad.scholaraid.domain.repository

import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.scholaraid.domain.model.UserProfile
import uk.ac.tees.mad.scholaraid.util.Resource

interface UserRepository {
    fun saveUserProfile(userProfile: UserProfile): Flow<Resource<Boolean>>
    fun getUserProfile(userId: String): Flow<Resource<UserProfile>>
    fun uploadProfileImage(userId: String, imageBytes: ByteArray): Flow<Resource<String>>
}