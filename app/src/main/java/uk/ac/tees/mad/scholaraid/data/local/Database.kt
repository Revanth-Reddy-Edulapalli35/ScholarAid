package uk.ac.tees.mad.scholaraid.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import uk.ac.tees.mad.scholaraid.data.local.dao.ScholarshipDao
import uk.ac.tees.mad.scholaraid.data.local.entities.ScholarshipEntity

@Database(
    entities = [ScholarshipEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ScholarshipDatabase : RoomDatabase() {

    abstract fun scholarshipDao(): ScholarshipDao

    companion object {
        @Volatile
        private var INSTANCE: ScholarshipDatabase? = null

        fun getInstance(context: Context): ScholarshipDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ScholarshipDatabase::class.java,
                    "scholarship_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}