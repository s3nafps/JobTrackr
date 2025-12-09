package ms.dev.jobtrackerpro.domain.usecase

import ms.dev.jobtrackerpro.domain.model.ApplicationStatus
import ms.dev.jobtrackerpro.domain.model.JobApplication
import ms.dev.jobtrackerpro.domain.model.StatusHistory
import ms.dev.jobtrackerpro.domain.repository.ApplicationRepository
import ms.dev.jobtrackerpro.domain.repository.StatusHistoryRepository
import javax.inject.Inject

/**
 * Use case for saving (creating or updating) a job application.
 * Validates required fields and manages timestamps.
 */
class SaveApplicationUseCase @Inject constructor(
    private val applicationRepository: ApplicationRepository,
    private val statusHistoryRepository: StatusHistoryRepository
) {
    suspend operator fun invoke(application: JobApplication): Result<Long> {
        // Validate required fields
        if (application.companyName.isBlank()) {
            return Result.failure(IllegalArgumentException("Company name is required"))
        }
        if (application.jobTitle.isBlank()) {
            return Result.failure(IllegalArgumentException("Job title is required"))
        }
        
        return try {
            val isNewApplication = application.id == 0L
            val currentTime = System.currentTimeMillis()
            
            val applicationToSave = if (isNewApplication) {
                application.copy(
                    createdTimestamp = currentTime,
                    updatedTimestamp = currentTime
                )
            } else {
                application.copy(updatedTimestamp = currentTime)
            }
            
            val applicationId = if (isNewApplication) {
                applicationRepository.insertApplication(applicationToSave)
            } else {
                applicationRepository.updateApplication(applicationToSave)
                application.id
            }
            
            // Create initial status history for new applications
            if (isNewApplication) {
                val statusHistory = StatusHistory(
                    applicationId = applicationId,
                    status = application.status,
                    statusDate = application.applicationDate,
                    notes = "Application created",
                    timestamp = currentTime
                )
                statusHistoryRepository.insertStatusHistory(statusHistory)
            }
            
            Result.success(applicationId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
