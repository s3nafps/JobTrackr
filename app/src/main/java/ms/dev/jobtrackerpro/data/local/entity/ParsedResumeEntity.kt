package ms.dev.jobtrackerpro.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "parsed_resumes",
    indices = [
        Index(value = ["is_active"]),
        Index(value = ["parsed_timestamp"])
    ]
)
data class ParsedResumeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "full_name")
    val fullName: String? = null,
    
    @ColumnInfo(name = "email")
    val email: String? = null,
    
    @ColumnInfo(name = "phone")
    val phone: String? = null,
    
    @ColumnInfo(name = "location")
    val location: String? = null,
    
    @ColumnInfo(name = "summary")
    val summary: String? = null,
    
    @ColumnInfo(name = "skills")
    val skills: String = "[]", // JSON array
    
    @ColumnInfo(name = "experiences")
    val experiences: String = "[]", // JSON array
    
    @ColumnInfo(name = "education")
    val education: String = "[]", // JSON array
    
    @ColumnInfo(name = "certifications")
    val certifications: String = "[]", // JSON array
    
    @ColumnInfo(name = "ats_score")
    val atsScore: Int? = null,
    
    @ColumnInfo(name = "parsed_timestamp")
    val parsedTimestamp: Long,
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true
)
