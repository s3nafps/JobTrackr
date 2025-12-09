package ms.dev.jobtrackerpro.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "communications",
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
        Index(value = ["communication_date"])
    ]
)
data class CommunicationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "application_id")
    val applicationId: Long,
    
    @ColumnInfo(name = "recruiter_name")
    val recruiterName: String? = null,
    
    @ColumnInfo(name = "recruiter_email")
    val recruiterEmail: String? = null,
    
    @ColumnInfo(name = "recruiter_phone")
    val recruiterPhone: String? = null,
    
    @ColumnInfo(name = "communication_type")
    val communicationType: String? = null,
    
    @ColumnInfo(name = "communication_date")
    val communicationDate: Long? = null,
    
    @ColumnInfo(name = "communication_notes")
    val communicationNotes: String? = null
)
