package ms.dev.jobtrackerpro.data.backup

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import ms.dev.jobtrackerpro.domain.model.ApplicationStatus
import ms.dev.jobtrackerpro.domain.model.Communication
import ms.dev.jobtrackerpro.domain.model.CommunicationType
import ms.dev.jobtrackerpro.domain.model.CompanySize
import ms.dev.jobtrackerpro.domain.model.JobApplication
import ms.dev.jobtrackerpro.domain.model.JobType
import ms.dev.jobtrackerpro.domain.model.RemoteStatus
import ms.dev.jobtrackerpro.domain.model.SalaryRange
import ms.dev.jobtrackerpro.domain.model.StatusHistory
import javax.inject.Inject

/**
 * Serializer for backup data.
 * Handles JSON serialization and deserialization of backup data.
 */
class BackupSerializer @Inject constructor() {
    
    private val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .create()
    
    /**
     * Serializes backup data to JSON string.
     */
    fun serialize(backupData: BackupData): String {
        return gson.toJson(backupData)
    }
    
    /**
     * Deserializes JSON string to backup data.
     */
    fun deserialize(json: String): BackupData {
        return gson.fromJson(json, BackupData::class.java)
    }
    
    /**
     * Creates backup data from domain models.
     */
    fun createBackupData(
        applications: List<JobApplication>,
        statusHistory: List<StatusHistory>,
        communications: List<Communication>
    ): BackupData {
        return BackupData(
            version = BackupData.CURRENT_VERSION,
            timestamp = System.currentTimeMillis(),
            applications = applications.map { it.toBackup() },
            statusHistory = statusHistory.map { it.toBackup() },
            communications = communications.map { it.toBackup() }
        )
    }
    
    /**
     * Restores domain models from backup data.
     */
    fun restoreFromBackup(backupData: BackupData): RestoredData {
        val applications = backupData.applications.map { it.toDomain() }
        val statusHistory = backupData.statusHistory.map { it.toDomain() }
        val communications = backupData.communications.map { it.toDomain() }
        
        return RestoredData(
            applications = applications,
            statusHistory = statusHistory,
            communications = communications
        )
    }
    
    // Extension functions for converting backup models to domain models
    
    private fun JobApplicationBackup.toDomain(): JobApplication {
        return JobApplication(
            id = id,
            companyName = companyName,
            jobTitle = jobTitle,
            applicationDate = applicationDate,
            status = ApplicationStatus.fromString(status),
            companyLocation = companyLocation,
            jobDescription = jobDescription,
            jobLink = jobLink,
            salaryRange = if (salaryMin != null || salaryMax != null) {
                SalaryRange(salaryMin, salaryMax)
            } else null,
            jobType = JobType.fromString(jobType),
            remoteStatus = RemoteStatus.fromString(remoteStatus),
            companySize = CompanySize.fromString(companySize),
            industry = industry,
            notes = notes,
            rating = rating,
            companyWebsite = companyWebsite,
            createdTimestamp = createdTimestamp,
            updatedTimestamp = updatedTimestamp
        )
    }
    
    private fun StatusHistoryBackup.toDomain(): StatusHistory {
        return StatusHistory(
            id = id,
            applicationId = applicationId,
            status = ApplicationStatus.fromString(status),
            statusDate = statusDate,
            notes = notes,
            timestamp = timestamp
        )
    }
    
    private fun CommunicationBackup.toDomain(): Communication {
        return Communication(
            id = id,
            applicationId = applicationId,
            recruiterName = recruiterName,
            recruiterEmail = recruiterEmail,
            recruiterPhone = recruiterPhone,
            communicationType = CommunicationType.fromString(communicationType),
            communicationDate = communicationDate,
            communicationNotes = communicationNotes
        )
    }
    
}

/**
 * Container for restored data from backup.
 */
data class RestoredData(
    val applications: List<JobApplication>,
    val statusHistory: List<StatusHistory>,
    val communications: List<Communication>
)
