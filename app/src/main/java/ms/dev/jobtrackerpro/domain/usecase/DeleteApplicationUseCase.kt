package ms.dev.jobtrackerpro.domain.usecase

import kotlinx.coroutines.flow.first
import ms.dev.jobtrackerpro.domain.model.JobApplication
import ms.dev.jobtrackerpro.domain.repository.ApplicationRepository
import ms.dev.jobtrackerpro.utils.Constants
import javax.inject.Inject

/**
 * Use case for deleting a job application with undo support.
 * Stores deleted application temporarily for potential restoration.
 */
class DeleteApplicationUseCase @Inject constructor(
    private val applicationRepository: ApplicationRepository
) {
    // Temporary storage for undo functionality
    private var deletedApplication: JobApplication? = null
    private var deletionTimestamp: Long = 0L
    
    suspend operator fun invoke(applicationId: Long): Result<JobApplication?> {
        return try {
            // Get application before deletion for undo
            val application = applicationRepository.getApplicationById(applicationId).first()
            
            if (application != null) {
                // Store for potential undo
                deletedApplication = application
                deletionTimestamp = System.currentTimeMillis()
                
                // Delete the application
                applicationRepository.deleteApplication(applicationId)
            }
            
            Result.success(application)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Restores the last deleted application if within undo timeout.
     */
    suspend fun undo(): Result<Long?> {
        val application = deletedApplication
        val timestamp = deletionTimestamp
        
        if (application == null) {
            return Result.failure(IllegalStateException("No application to restore"))
        }
        
        val currentTime = System.currentTimeMillis()
        if (currentTime - timestamp > Constants.UNDO_TIMEOUT_MS) {
            // Clear stored application after timeout
            deletedApplication = null
            return Result.failure(IllegalStateException("Undo timeout expired"))
        }
        
        return try {
            // Restore the application
            val newId = applicationRepository.insertApplication(application)
            
            // Clear stored application
            deletedApplication = null
            
            Result.success(newId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Clears the stored deleted application (called after undo timeout).
     */
    fun clearUndoState() {
        deletedApplication = null
        deletionTimestamp = 0L
    }
    
    /**
     * Checks if undo is available.
     */
    fun canUndo(): Boolean {
        val application = deletedApplication ?: return false
        val currentTime = System.currentTimeMillis()
        return currentTime - deletionTimestamp <= Constants.UNDO_TIMEOUT_MS
    }
}
