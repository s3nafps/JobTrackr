package ms.dev.jobtrackerpro.domain.model

import androidx.compose.runtime.Immutable

/**
 * Domain model for a job application.
 * OPTIMIZATION: @Immutable annotation tells Compose this class won't change,
 * enabling skip optimizations during recomposition for 120fps scrolling
 */
@Immutable
data class JobApplication(
    val id: Long = 0,
    val companyName: String,
    val jobTitle: String,
    val applicationDate: Long,
    val status: ApplicationStatus,
    val companyLocation: String? = null,
    val jobDescription: String? = null,
    val jobLink: String? = null,
    val salaryRange: SalaryRange? = null,
    val jobType: JobType? = null,
    val remoteStatus: RemoteStatus? = null,
    val companySize: CompanySize? = null,
    val industry: String? = null,
    val notes: String? = null,
    val rating: Int? = null,
    val companyWebsite: String? = null,
    val createdTimestamp: Long = System.currentTimeMillis(),
    val updatedTimestamp: Long = System.currentTimeMillis()
)

/**
 * Salary range data class.
 * OPTIMIZATION: @Immutable for Compose skip optimizations
 */
@Immutable
data class SalaryRange(
    val min: Int?,
    val max: Int?
) {
    fun toDisplayString(): String {
        return when {
            min != null && max != null -> "$${min.formatSalary()} - $${max.formatSalary()}"
            min != null -> "From $${min.formatSalary()}"
            max != null -> "Up to $${max.formatSalary()}"
            else -> "Not specified"
        }
    }
    
    private fun Int.formatSalary(): String {
        return when {
            this >= 1000 -> "${this / 1000}k"
            else -> this.toString()
        }
    }
}

/**
 * Status history domain model.
 */
data class StatusHistory(
    val id: Long = 0,
    val applicationId: Long,
    val status: ApplicationStatus,
    val statusDate: Long,
    val notes: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Communication domain model.
 */
data class Communication(
    val id: Long = 0,
    val applicationId: Long,
    val recruiterName: String? = null,
    val recruiterEmail: String? = null,
    val recruiterPhone: String? = null,
    val communicationType: CommunicationType? = null,
    val communicationDate: Long? = null,
    val communicationNotes: String? = null
)

/**
 * Cloud backup domain model.
 */
data class CloudBackup(
    val id: Long = 0,
    val backupType: BackupType,
    val backupTimestamp: Long,
    val backupStatus: BackupStatus,
    val backupFileId: String? = null,
    val backupLocation: String? = null
)
