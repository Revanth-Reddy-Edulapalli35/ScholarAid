package uk.ac.tees.mad.scholaraid.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import uk.ac.tees.mad.scholaraid.data.local.dao.ScholarshipDao
import uk.ac.tees.mad.scholaraid.data.local.entities.ScholarshipEntity
import uk.ac.tees.mad.scholaraid.data.model.Scholarship
import uk.ac.tees.mad.scholaraid.domain.repository.LocalScholarshipRepository
import javax.inject.Inject

class LocalScholarshipRepositoryImpl @Inject constructor(
    private val scholarshipDao: ScholarshipDao
) : LocalScholarshipRepository {

    override fun getAllSavedScholarships(): Flow<List<Scholarship>> {
        return scholarshipDao.getAllSavedScholarships().map { entities ->
            entities.map { it.toScholarship() }
        }
    }

    override suspend fun saveScholarship(scholarship: Scholarship) {
        scholarshipDao.saveScholarship(scholarship.toEntity())
    }

    override suspend fun unsaveScholarship(scholarshipId: String) {
        scholarshipDao.unsaveScholarship(scholarshipId)
    }

    override suspend fun isScholarshipSaved(scholarshipId: String): Boolean {
        return scholarshipDao.isScholarshipSaved(scholarshipId) > 0
    }

    override suspend fun clearAll() {
        scholarshipDao.clearAll()
    }
}

// Extension functions for conversion
private fun ScholarshipEntity.toScholarship(): Scholarship {
    return Scholarship(
        id = this.id,
        title = this.title,
        provider = this.provider,
        amount = this.amount,
        country = this.country,
        deadline = this.deadline,
        applicationLink = this.applicationLink,
        description = this.description
    )
}

private fun Scholarship.toEntity(): ScholarshipEntity {
    return ScholarshipEntity(
        id = this.id,
        title = this.title,
        provider = this.provider,
        amount = this.amount,
        country = this.country,
        deadline = this.deadline,
        applicationLink = this.applicationLink,
        description = this.description
    )
}