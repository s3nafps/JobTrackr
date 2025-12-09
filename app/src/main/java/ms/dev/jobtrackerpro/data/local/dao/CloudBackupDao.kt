package ms.dev.jobtrackerpro.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ms.dev.jobtrackerpro.data.local.entity.CloudBackupEntity

@Dao
interface CloudBackupDao {
    
    @Query("SELECT * FROM cloud_backups ORDER BY backup_timestamp DESC")
    fun getAllBackups(): Flow<List<CloudBackupEntity>>
    
    @Query("SELECT * FROM cloud_backups WHERE backup_status = 'COMPLETED' ORDER BY backup_timestamp DESC LIMIT 1")
    fun getLastSuccessfulBackup(): Flow<CloudBackupEntity?>
    
    @Query("SELECT * FROM cloud_backups WHERE backup_type = :type ORDER BY backup_timestamp DESC")
    fun getBackupsByType(type: String): Flow<List<CloudBackupEntity>>
    
    @Query("SELECT * FROM cloud_backups WHERE id = :id")
    fun getBackupById(id: Long): Flow<CloudBackupEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(backup: CloudBackupEntity): Long
    
    @Update
    suspend fun update(backup: CloudBackupEntity)
    
    @Delete
    suspend fun delete(backup: CloudBackupEntity)
    
    @Query("DELETE FROM cloud_backups WHERE id = :id")
    suspend fun deleteById(id: Long)
    
    @Query("DELETE FROM cloud_backups")
    suspend fun deleteAll()
}
