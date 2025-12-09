package ms.dev.jobtrackerpro.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ms.dev.jobtrackerpro.data.local.entity.CommunicationEntity

@Dao
interface CommunicationDao {
    
    @Query("SELECT * FROM communications WHERE application_id = :applicationId ORDER BY communication_date DESC")
    fun getCommunications(applicationId: Long): Flow<List<CommunicationEntity>>
    
    @Query("SELECT * FROM communications ORDER BY communication_date DESC")
    fun getAllCommunications(): Flow<List<CommunicationEntity>>
    
    @Query("SELECT * FROM communications WHERE id = :id")
    fun getCommunicationById(id: Long): Flow<CommunicationEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(communication: CommunicationEntity): Long
    
    @Update
    suspend fun update(communication: CommunicationEntity)
    
    @Delete
    suspend fun delete(communication: CommunicationEntity)
    
    @Query("DELETE FROM communications WHERE id = :id")
    suspend fun deleteById(id: Long)
    
    @Query("DELETE FROM communications WHERE application_id = :applicationId")
    suspend fun deleteByApplicationId(applicationId: Long)
    
    @Query("DELETE FROM communications")
    suspend fun deleteAll()
}
