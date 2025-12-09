package ms.dev.jobtrackerpro.data.backup

import ms.dev.jobtrackerpro.domain.model.Communication
import ms.dev.jobtrackerpro.domain.model.JobApplication
import ms.dev.jobtrackerpro.domain.model.StatusHistory

/**
 * Data class representing the complete backup structure.
 */
data class BackupData(
    val version: Int = CURRENT_VERSION,
    val timestamp: Long = System.currentTimeMillis(),
    val applications: List<JobApplicationBackup> = emptyList(),
    val statusHistory: List<StatusHistoryBackup> = emptyList(),
    val communications: List<CommunicationBackup> = emptyList()
) {
    companion object {
        const val CURRENT_VERSION = 1
    }
}

/**
 * Backup model for JobApplication.
 */
data class JobApplicationBackup(
    val id: Long,
    val companyName: String,
    val jobTitle: String,
    val applicationDate: Long,
    val status: String,
    val companyLocation: String?,
    val jobDescription: String?,
    val jobLink: String?,
    val salaryMin: Int?,
    val salaryMax: Int?,
    val jobType: String?,
    val remoteStatus: String?,
    val companySize: String?,
    val industry: String?,
    val notes: String?,
    val rating: Int?,
    val companyWebsite: String?,
    val createdTimestamp: Long,
    val updatedTimestamp: Long
)

/**
 * Backup model for StatusHistory.
 */
data class StatusHistoryBackup(
    val id: Long,
    val applicationId: Long,
    val status: String,
    val statusDate: Long,
    val notes: String?,
    val timestamp: Long
)

/**
 * Backup model for Communication.
 */
data class CommunicationBackup(
    val id: Long,
    val applicationId: Long,
    val recruiterName: String?,
    val recruiterEmail: String?,
    val recruiterPhone: String?,
    val communicationType: String?,
    val communicationDate: Long?,
    val communicationNotes: String?
)

// Extension functions for converting domain models to backup models

fun JobApplication.toBackup(): JobApplicationBackup {
    return JobApplicationBackup(
        id = id,
        companyName = companyName,
        jobTitle = jobTitle,
        applicationDate = applicationDate,
        status = status.name,
        companyLocation = companyLocation,
        jobDescription = jobDescription,
        jobLink = jobLink,
        salaryMin = salaryRange?.min,
        salaryMax = salaryRange?.max,
        jobType = jobType?.name,
        remoteStatus = remoteStatus?.name,
        companySize = companySize?.name,
        industry = industry,
        notes = notes,
        rating = rating,
        companyWebsite = companyWebsite,
        createdTimestamp = createdTimestamp,
        updatedTimestamp = updatedTimestamp
    )
}

fun StatusHistory.toBackup(): StatusHistoryBackup {
    return StatusHistoryBackup(
        id = id,
        applicationId = applicationId,
        status = status.name,
        statusDate = statusDate,
        notes = notes,
        timestamp = timestamp
    )
}

fun Communication.toBackup(): CommunicationBackup {
    return CommunicationBackup(
        id = id,
        applicationId = applicationId,
        recruiterName = recruiterName,
        recruiterEmail = recruiterEmail,
        recruiterPhone = recruiterPhone,
        communicationType = communicationType?.name,
        communicationDate = communicationDate,
        communicationNotes = communicationNotes
    )
}
