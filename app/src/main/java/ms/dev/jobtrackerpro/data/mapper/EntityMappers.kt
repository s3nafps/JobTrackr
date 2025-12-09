package ms.dev.jobtrackerpro.data.mapper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ms.dev.jobtrackerpro.data.local.entity.CloudBackupEntity
import ms.dev.jobtrackerpro.data.local.entity.CommunicationEntity
import ms.dev.jobtrackerpro.data.local.entity.JobApplicationEntity
import ms.dev.jobtrackerpro.data.local.entity.ParsedResumeEntity
import ms.dev.jobtrackerpro.data.local.entity.StatusHistoryEntity
import ms.dev.jobtrackerpro.domain.model.ApplicationStatus
import ms.dev.jobtrackerpro.domain.model.BackupStatus
import ms.dev.jobtrackerpro.domain.model.BackupType
import ms.dev.jobtrackerpro.domain.model.CloudBackup
import ms.dev.jobtrackerpro.domain.model.Communication
import ms.dev.jobtrackerpro.domain.model.CommunicationType
import ms.dev.jobtrackerpro.domain.model.CompanySize
import ms.dev.jobtrackerpro.domain.model.Education
import ms.dev.jobtrackerpro.domain.model.Experience
import ms.dev.jobtrackerpro.domain.model.JobApplication
import ms.dev.jobtrackerpro.domain.model.JobType
import ms.dev.jobtrackerpro.domain.model.ParsedResume
import ms.dev.jobtrackerpro.domain.model.RemoteStatus
import ms.dev.jobtrackerpro.domain.model.SalaryRange
import ms.dev.jobtrackerpro.domain.model.StatusHistory

private val gson = Gson()

// JobApplication Mappers
fun JobApplicationEntity.toDomain(): JobApplication {
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

fun JobApplication.toEntity(): JobApplicationEntity {
    return JobApplicationEntity(
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

// StatusHistory Mappers
fun StatusHistoryEntity.toDomain(): StatusHistory {
    return StatusHistory(
        id = id,
        applicationId = applicationId,
        status = ApplicationStatus.fromString(status),
        statusDate = statusDate,
        notes = notes,
        timestamp = timestamp
    )
}

fun StatusHistory.toEntity(): StatusHistoryEntity {
    return StatusHistoryEntity(
        id = id,
        applicationId = applicationId,
        status = status.name,
        statusDate = statusDate,
        notes = notes,
        timestamp = timestamp
    )
}

// Communication Mappers
fun CommunicationEntity.toDomain(): Communication {
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

fun Communication.toEntity(): CommunicationEntity {
    return CommunicationEntity(
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

// ParsedResume Mappers
fun ParsedResumeEntity.toDomain(): ParsedResume {
    val experienceType = object : TypeToken<List<Experience>>() {}.type
    val educationType = object : TypeToken<List<Education>>() {}.type
    val stringListType = object : TypeToken<List<String>>() {}.type
    
    return ParsedResume(
        id = id,
        fullName = fullName,
        email = email,
        phone = phone,
        location = location,
        summary = summary,
        skills = try { gson.fromJson(skills, stringListType) ?: emptyList() } catch (e: Exception) { emptyList() },
        experiences = try { gson.fromJson(experiences, experienceType) ?: emptyList() } catch (e: Exception) { emptyList() },
        education = try { gson.fromJson(education, educationType) ?: emptyList() } catch (e: Exception) { emptyList() },
        certifications = try { gson.fromJson(certifications, stringListType) ?: emptyList() } catch (e: Exception) { emptyList() },
        atsScore = atsScore,
        parsedTimestamp = parsedTimestamp,
        isActive = isActive
    )
}

fun ParsedResume.toEntity(): ParsedResumeEntity {
    return ParsedResumeEntity(
        id = id,
        fullName = fullName,
        email = email,
        phone = phone,
        location = location,
        summary = summary,
        skills = gson.toJson(skills),
        experiences = gson.toJson(experiences),
        education = gson.toJson(education),
        certifications = gson.toJson(certifications),
        atsScore = atsScore,
        parsedTimestamp = parsedTimestamp,
        isActive = isActive
    )
}

// CloudBackup Mappers
fun CloudBackupEntity.toDomain(): CloudBackup {
    return CloudBackup(
        id = id,
        backupType = BackupType.fromString(backupType),
        backupTimestamp = backupTimestamp,
        backupStatus = BackupStatus.fromString(backupStatus),
        backupFileId = backupFileId,
        backupLocation = backupLocation
    )
}

fun CloudBackup.toEntity(): CloudBackupEntity {
    return CloudBackupEntity(
        id = id,
        backupType = backupType.name,
        backupTimestamp = backupTimestamp,
        backupStatus = backupStatus.name,
        backupFileId = backupFileId,
        backupLocation = backupLocation
    )
}

// List extension functions
fun List<JobApplicationEntity>.toDomainList(): List<JobApplication> = map { it.toDomain() }
fun List<StatusHistoryEntity>.toStatusHistoryDomainList(): List<StatusHistory> = map { it.toDomain() }
fun List<CommunicationEntity>.toCommunicationDomainList(): List<Communication> = map { it.toDomain() }
fun List<CloudBackupEntity>.toBackupDomainList(): List<CloudBackup> = map { it.toDomain() }
