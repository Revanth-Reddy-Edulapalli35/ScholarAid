package uk.ac.tees.mad.scholaraid.domain.repository

import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.scholaraid.domain.model.UserProfile
import uk.ac.tees.mad.scholaraid.util.Resource

interface UserRepository {
    suspend fun saveUserProfile(userProfile: UserProfile): Flow<Resource<Boolean>>
    fun getUserProfile(userId: String): Flow<Resource<UserProfile>>

    suspend fun updateUserProfile(userProfile: UserProfile): Resource<Boolean>

    fun getSavedScholarshipIds(userId: String): Flow<Resource<List<String>>>
    suspend fun saveScholarship(userId: String, scholarshipId: String): Resource<Boolean>
    suspend fun unsaveScholarship(userId: String, scholarshipId: String): Resource<Boolean>
}