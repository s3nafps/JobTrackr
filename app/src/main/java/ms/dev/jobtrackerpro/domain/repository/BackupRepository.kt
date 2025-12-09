package ms.dev.jobtrackerpro.domain.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import ms.dev.jobtrackerpro.domain.model.BackupType
import ms.dev.jobtrackerpro.domain.model.CloudBackup

interface BackupRepository {
    
    fun getAllBackups(): Flow<List<CloudBackup>>
    
    fun getLastSuccessfulBackup(): Flow<CloudBackup?>
    
    fun getBackupsByType(type: BackupType): Flow<List<CloudBackup>>
    
    suspend fun createBackup(type: BackupType): Result<CloudBackup>
    
    suspend fun exportToUri(uri: Uri): Result<CloudBackup>
    
    suspend fun importFromUri(uri: Uri): Result<Int>
    
    suspend fun insertBackupRecord(backup: CloudBackup): Long
    
    suspend fun updateBackupRecord(backup: CloudBackup)
    
    suspend fun deleteBackup(id: Long)
    
    suspend fun deleteAll()
}
