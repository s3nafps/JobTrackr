package ms.dev.jobtrackerpro.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "status_history",
    foreignKeys = [
        ForeignKey(
            entity = JobApplicationEntity::class,
            parentColumns = ["id"],
            childColumns = ["application_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["application_id"]),
        Index(value = ["status_date"])
    ]
)
data class StatusHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "application_id")
    val applicationId: Long,
    
    @ColumnInfo(name = "status")
    val status: String,
    
    @ColumnInfo(name = "status_date")
    val statusDate: Long,
    
    @ColumnInfo(name = "notes")
    val notes: String? = null,
    
    @ColumnInfo(name = "timestamp")
    val timestamp: Long
)
