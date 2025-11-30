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

    @Query("SELECT * FROM scholarships ORDER BY cachedAt DESC")
    fun getAllScholarships(): Flow<List<ScholarshipEntity>>

    @Query("SELECT * FROM scholarships WHERE isSaved = 1 ORDER BY cachedAt DESC")
    fun getSavedScholarships(): Flow<List<ScholarshipEntity>>

    @Query("SELECT * FROM scholarships WHERE id = :id")
    suspend fun getScholarshipById(id: String): ScholarshipEntity?

    @Query("SELECT * FROM scholarships WHERE field LIKE '%' || :field || '%' OR educationLevel LIKE '%' || :level || '%' OR country LIKE '%' || :country || '%'")
    fun getFilteredScholarships(field: String? = null, level: String? = null, country: String? = null): Flow<List<ScholarshipEntity>>

    @Query("SELECT * FROM scholarships WHERE title LIKE '%' || :query || '%' OR provider LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchScholarships(query: String): Flow<List<ScholarshipEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScholarship(scholarship: ScholarshipEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(scholarships: List<ScholarshipEntity>)

    @Update
    suspend fun updateScholarship(scholarship: ScholarshipEntity)

    @Query("UPDATE scholarships SET isSaved = :isSaved WHERE id = :id")
    suspend fun updateSavedStatus(id: String, isSaved: Boolean)

    @Delete
    suspend fun deleteScholarship(scholarship: ScholarshipEntity)

    @Query("DELETE FROM scholarships WHERE isSaved = 0 AND cachedAt < :timestamp")
    suspend fun deleteOldCache(timestamp: Long)

    @Query("SELECT COUNT(*) FROM scholarships")
    suspend fun getCount(): Int

    @Query("DELETE FROM scholarships WHERE isSaved = 0")
    suspend fun clearUnsavedCache()
}