package uk.ac.tees.mad.scholaraid.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.scholaraid.data.local.entities.ScholarshipEntity

@Dao
interface ScholarshipDao {

    @Query("SELECT * FROM saved_scholarships ORDER BY savedAt DESC")
    fun getAllSavedScholarships(): Flow<List<ScholarshipEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveScholarship(scholarship: ScholarshipEntity)

    @Query("DELETE FROM saved_scholarships WHERE id = :scholarshipId")
    suspend fun unsaveScholarship(scholarshipId: String)

    @Query("SELECT * FROM saved_scholarships WHERE id = :scholarshipId")
    suspend fun getScholarshipById(scholarshipId: String): ScholarshipEntity?

    @Query("SELECT COUNT(*) FROM saved_scholarships WHERE id = :scholarshipId")
    suspend fun isScholarshipSaved(scholarshipId: String): Int

    @Query("DELETE FROM saved_scholarships")
    suspend fun clearAll()
}