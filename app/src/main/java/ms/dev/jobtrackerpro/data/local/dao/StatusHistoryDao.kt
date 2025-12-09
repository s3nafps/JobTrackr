package ms.dev.jobtrackerpro.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ms.dev.jobtrackerpro.data.local.entity.StatusHistoryEntity

@Dao
interface StatusHistoryDao {
    
    @Query("SELECT * FROM status_history WHERE application_id = :applicationId ORDER BY status_date ASC")
    fun getStatusHistory(applicationId: Long): Flow<List<StatusHistoryEntity>>
    
    @Query("SELECT * FROM status_history ORDER BY timestamp DESC")
    fun getAllStatusHistory(): Flow<List<StatusHistoryEntity>>
    
    @Query("""
        SELECT * FROM status_history 
        WHERE status_date BETWEEN :startDate AND :endDate 
        ORDER BY status_date ASC
    """)
    fun getStatusHistoryByDateRange(startDate: Long, endDate: Long): Flow<List<StatusHistoryEntity>>
    
    @Query("""
        SELECT AVG(sh2.status_date - sh1.status_date) as avg_days
        FROM status_history sh1
        INNER JOIN status_history sh2 ON sh1.application_id = sh2.application_id
        WHERE sh1.status = :fromStatus AND sh2.status = :toStatus
        AND sh2.status_date > sh1.status_date
    """)
    suspend fun getAverageTransitionTime(fromStatus: String, toStatus: String): Long?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(statusHistory: StatusHistoryEntity): Long
    
    @Query("DELETE FROM status_history WHERE application_id = :applicationId")
    suspend fun deleteByApplicationId(applicationId: Long)
    
    @Query("DELETE FROM status_history")
    suspend fun deleteAll()
}
