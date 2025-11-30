package uk.ac.tees.mad.scholaraid.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "scholarships")
data class ScholarshipEntity(
    @PrimaryKey val id: String,
    val title: String,
    val provider: String,
    val description: String,
    val eligibility: String,
    val amount: String,
    val deadline: String,
    val country: String,
    val field: String,
    val educationLevel: String,
    val applicationLink: String,
    val isSaved: Boolean,
    val cachedAt: Long = System.currentTimeMillis(),
    val lastUpdated: Long = System.currentTimeMillis()
)