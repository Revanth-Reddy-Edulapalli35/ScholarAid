package uk.ac.tees.mad.scholaraid.domain.repository

import uk.ac.tees.mad.scholaraid.data.model.Scholarship

interface ScholarshipRepository {
    suspend fun getScholarships(): List<Scholarship>

    suspend fun getScholarshipById(scholarshipId: String): Scholarship?

    suspend fun getScholarshipsByIds(ids: List<String>): List<Scholarship>

}