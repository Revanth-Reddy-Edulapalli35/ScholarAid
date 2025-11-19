package uk.ac.tees.mad.scholaraid.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.scholaraid.data.local.entities.ScholarshipEntity

@Dao
interface ScholarshipDao {
    @Query("SELECT * FROM scholarships")
    fun getAllScholarships(): Flow<List<ScholarshipEntity>>

    @Query("SELECT * FROM scholarships WHERE isSaved = 1")
    fun getSavedScholarships(): Flow<List<ScholarshipEntity>>

    @Query("SELECT * FROM scholarships WHERE id = :id")
    suspend fun getScholarshipById(id: String): ScholarshipEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScholarship(scholarship: ScholarshipEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(scholarships: List<ScholarshipEntity>)

    @Update
    suspend fun updateScholarship(scholarship: ScholarshipEntity)

    @Delete
    suspend fun deleteScholarship(scholarship: ScholarshipEntity)

    @Query("DELETE FROM scholarships WHERE isSaved = 0 AND cachedAt < :timestamp")
    suspend fun deleteOldCache(timestamp: Long)
}