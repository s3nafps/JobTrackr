package ms.dev.jobtrackerpro.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ms.dev.jobtrackerpro.data.local.entity.ParsedResumeEntity

@Dao
interface ParsedResumeDao {
    
    @Query("SELECT * FROM parsed_resumes WHERE is_active = 1 ORDER BY parsed_timestamp DESC LIMIT 1")
    fun getActiveResume(): Flow<ParsedResumeEntity?>
    
    @Query("SELECT * FROM parsed_resumes ORDER BY parsed_timestamp DESC")
    fun getAllResumes(): Flow<List<ParsedResumeEntity>>
    
    @Query("SELECT * FROM parsed_resumes WHERE id = :id")
    fun getResumeById(id: Long): Flow<ParsedResumeEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(resume: ParsedResumeEntity): Long
    
    @Update
    suspend fun update(resume: ParsedResumeEntity)
    
    @Delete
    suspend fun delete(resume: ParsedResumeEntity)
    
    @Query("DELETE FROM parsed_resumes WHERE id = :id")
    suspend fun deleteById(id: Long)
    
    @Query("UPDATE parsed_resumes SET is_active = 0")
    suspend fun deactivateAll()
    
    @Query("DELETE FROM parsed_resumes")
    suspend fun deleteAll()
}
