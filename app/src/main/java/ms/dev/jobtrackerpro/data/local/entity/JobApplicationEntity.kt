package ms.dev.jobtrackerpro.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "job_applications",
    indices = [
        Index(value = ["company_name"]),
        Index(value = ["status"]),
        Index(value = ["application_date"]),
        Index(value = ["updated_timestamp"])
    ]
)
data class JobApplicationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "company_name")
    val companyName: String,
    
    @ColumnInfo(name = "job_title")
    val jobTitle: String,
    
    @ColumnInfo(name = "application_date")
    val applicationDate: Long,
    
    @ColumnInfo(name = "status")
    val status: String,
    
    @ColumnInfo(name = "company_location")
    val companyLocation: String? = null,
    
    @ColumnInfo(name = "job_description")
    val jobDescription: String? = null,
    
    @ColumnInfo(name = "job_link")
    val jobLink: String? = null,
    
    @ColumnInfo(name = "salary_min")
    val salaryMin: Int? = null,
    
    @ColumnInfo(name = "salary_max")
    val salaryMax: Int? = null,
    
    @ColumnInfo(name = "job_type")
    val jobType: String? = null,
    
    @ColumnInfo(name = "remote_status")
    val remoteStatus: String? = null,
    
    @ColumnInfo(name = "company_size")
    val companySize: String? = null,
    
    @ColumnInfo(name = "industry")
    val industry: String? = null,
    
    @ColumnInfo(name = "notes")
    val notes: String? = null,
    
    @ColumnInfo(name = "rating")
    val rating: Int? = null,
    
    @ColumnInfo(name = "company_website")
    val companyWebsite: String? = null,
    
    @ColumnInfo(name = "created_timestamp")
    val createdTimestamp: Long,
    
    @ColumnInfo(name = "updated_timestamp")
    val updatedTimestamp: Long
)
