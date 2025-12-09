package ms.dev.jobtrackerpro.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ms.dev.jobtrackerpro.data.local.entity.JobApplicationEntity

@Dao
interface JobApplicationDao {
    
    @Query("SELECT * FROM job_applications ORDER BY updated_timestamp DESC")
    fun getAllApplications(): Flow<List<JobApplicationEntity>>
    
    @Query("SELECT * FROM job_applications ORDER BY updated_timestamp DESC")
    suspend fun getAllApplicationsSync(): List<JobApplicationEntity>
    
    @Query("SELECT * FROM job_applications WHERE id = :id")
    fun getApplicationById(id: Long): Flow<JobApplicationEntity?>
    
    @Query("SELECT * FROM job_applications ORDER BY updated_timestamp DESC LIMIT :limit")
    fun getRecentApplications(limit: Int): Flow<List<JobApplicationEntity>>
    
    @Query("""
        SELECT * FROM job_applications 
        WHERE company_name LIKE '%' || :query || '%' 
        OR job_title LIKE '%' || :query || '%'
        OR notes LIKE '%' || :query || '%'
        ORDER BY updated_timestamp DESC
    """)
    fun searchApplications(query: String): Flow<List<JobApplicationEntity>>
    
    @Query("SELECT * FROM job_applications WHERE status = :status ORDER BY updated_timestamp DESC")
    fun getApplicationsByStatus(status: String): Flow<List<JobApplicationEntity>>
    
    @Query("""
        SELECT * FROM job_applications 
        WHERE application_date BETWEEN :startDate AND :endDate 
        ORDER BY updated_timestamp DESC
    """)
    fun getApplicationsByDateRange(startDate: Long, endDate: Long): Flow<List<JobApplicationEntity>>
    
    @Query("SELECT * FROM job_applications ORDER BY application_date DESC")
    fun getApplicationsSortedByNewest(): Flow<List<JobApplicationEntity>>
    
    @Query("SELECT * FROM job_applications ORDER BY application_date ASC")
    fun getApplicationsSortedByOldest(): Flow<List<JobApplicationEntity>>
    
    @Query("SELECT * FROM job_applications ORDER BY company_name ASC")
    fun getApplicationsSortedByCompany(): Flow<List<JobApplicationEntity>>
    
    @Query("SELECT * FROM job_applications ORDER BY status ASC")
    fun getApplicationsSortedByStatus(): Flow<List<JobApplicationEntity>>
    
    @Query("SELECT COUNT(*) FROM job_applications")
    fun getTotalCount(): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM job_applications WHERE status = :status")
    fun getCountByStatus(status: String): Flow<Int>
    
    @Query("SELECT status, COUNT(*) as count FROM job_applications GROUP BY status")
    fun getStatusDistribution(): Flow<List<StatusCount>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(application: JobApplicationEntity): Long
    
    @Update
    suspend fun update(application: JobApplicationEntity)
    
    @Delete
    suspend fun delete(application: JobApplicationEntity)
    
    @Query("DELETE FROM job_applications WHERE id = :id")
    suspend fun deleteById(id: Long)
    
    @Query("DELETE FROM job_applications")
    suspend fun deleteAll()
}

data class StatusCount(
    val status: String,
    val count: Int
)
