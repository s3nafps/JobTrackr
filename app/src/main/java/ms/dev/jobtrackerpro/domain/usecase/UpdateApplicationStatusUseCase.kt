package ms.dev.jobtrackerpro.domain.usecase

import kotlinx.coroutines.flow.first
import ms.dev.jobtrackerpro.domain.model.ApplicationStatus
import ms.dev.jobtrackerpro.domain.model.StatusHistory
import ms.dev.jobtrackerpro.domain.repository.ApplicationRepository
import ms.dev.jobtrackerpro.domain.repository.StatusHistoryRepository
import javax.inject.Inject

/**
 * Use case for updating the status of a job application.
 * Creates a status history record with timestamp.
 */
class UpdateApplicationStatusUseCase @Inject constructor(
    private val applicationRepository: ApplicationRepository,
    private val statusHistoryRepository: StatusHistoryRepository
) {
    suspend operator fun invoke(
        applicationId: Long,
        newStatus: ApplicationStatus,
        statusDate: Long,
        notes: String? = null
    ): Result<Unit> {
        return try {
            // Get current application
            val application = applicationRepository.getApplicationById(applicationId).first()
                ?: return Result.failure(IllegalArgumentException("Application not found"))
            
            val currentTime = System.currentTimeMillis()
            
            // Update application status
            val updatedApplication = application.copy(
                status = newStatus,
                updatedTimestamp = currentTime
            )
            applicationRepository.updateApplication(updatedApplication)
            
            // Create status history record
            val statusHistory = StatusHistory(
                applicationId = applicationId,
                status = newStatus,
                statusDate = statusDate,
                notes = notes,
                timestamp = currentTime
            )
            statusHistoryRepository.insertStatusHistory(statusHistory)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
