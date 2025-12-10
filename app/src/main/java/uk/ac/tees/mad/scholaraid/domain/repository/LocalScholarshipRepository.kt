package uk.ac.tees.mad.scholaraid.domain.repository

import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.scholaraid.data.model.Scholarship

interface LocalScholarshipRepository {
    fun getAllSavedScholarships(): Flow<List<Scholarship>>
    suspend fun saveScholarship(scholarship: Scholarship)
    suspend fun unsaveScholarship(scholarshipId: String)
    suspend fun isScholarshipSaved(scholarshipId: String): Boolean
    suspend fun clearAll()
}