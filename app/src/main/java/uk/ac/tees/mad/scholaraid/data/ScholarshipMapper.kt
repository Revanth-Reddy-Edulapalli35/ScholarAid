package uk.ac.tees.mad.scholaraid.data

import uk.ac.tees.mad.scholaraid.data.local.entities.ScholarshipEntity
import uk.ac.tees.mad.scholaraid.data.model.Scholarship
import uk.ac.tees.mad.scholaraid.data.remote.dto.ScholarshipDto
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ScholarshipMapper {

    fun dtoToEntity(dto: ScholarshipDto, isSaved: Boolean = false, cachedAt: Long = System.currentTimeMillis()): ScholarshipEntity {
        return ScholarshipEntity(
            id = dto.id,
            title = dto.title,
            provider = dto.provider,
            description = dto.description,
            eligibility = dto.eligibility,
            amount = dto.amount,
            deadline = dto.deadline,
            country = dto.country,
            field = dto.field,
            educationLevel = dto.educationLevel,
            applicationLink = dto.applicationLink,
            isSaved = isSaved,
            cachedAt = cachedAt
        )
    }

    fun entityToDomain(entity: ScholarshipEntity): Scholarship {
        return Scholarship(
            id = entity.id,
            title = entity.title,
            provider = entity.provider,
            description = entity.description,
            eligibility = entity.eligibility,
            amount = entity.amount,
            deadline = entity.deadline,
            country = entity.country,
            field = entity.field,
            educationLevel = entity.educationLevel,
            applicationLink = entity.applicationLink,
            isSaved = entity.isSaved
        )
    }

    fun dtoToDomain(dto: ScholarshipDto, isSaved: Boolean = false): Scholarship {
        return Scholarship(
            id = dto.id,
            title = dto.title,
            provider = dto.provider,
            description = dto.description,
            eligibility = dto.eligibility,
            amount = dto.amount,
            deadline = dto.deadline,
            country = dto.country,
            field = dto.field,
            educationLevel = dto.educationLevel,
            applicationLink = dto.applicationLink,
            isSaved = isSaved
        )
    }

    // Helper function to format deadline
    fun formatDeadline(deadline: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val date = inputFormat.parse(deadline)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            deadline
        }
    }
}