package ms.dev.jobtrackerpro.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cloud_backups",
    indices = [
        Index(value = ["backup_timestamp"]),
        Index(value = ["backup_status"])
    ]
)
data class CloudBackupEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "backup_type")
    val backupType: String,
    
    @ColumnInfo(name = "backup_timestamp")
    val backupTimestamp: Long,
    
    @ColumnInfo(name = "backup_status")
    val backupStatus: String,
    
    @ColumnInfo(name = "backup_file_id")
    val backupFileId: String? = null,
    
    @ColumnInfo(name = "backup_location")
    val backupLocation: String? = null
)
