package uk.ac.tees.mad.scholaraid.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_scholarships")
data class ScholarshipEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val provider: String,
    val amount: String,
    val country: String,
    val deadline: String,
    val applicationLink: String,
    val description: String,
    val savedAt: Long = System.currentTimeMillis()
)