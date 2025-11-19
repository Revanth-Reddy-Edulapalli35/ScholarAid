package uk.ac.tees.mad.scholaraid.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import uk.ac.tees.mad.scholaraid.data.local.dao.ScholarshipDao
import uk.ac.tees.mad.scholaraid.data.local.entities.ScholarshipEntity

@Database(
    entities = [ScholarshipEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ScholarAidDatabase : RoomDatabase() {
    abstract fun scholarshipDao(): ScholarshipDao
}