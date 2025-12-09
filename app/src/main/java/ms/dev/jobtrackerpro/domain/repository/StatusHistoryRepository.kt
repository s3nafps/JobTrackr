package ms.dev.jobtrackerpro.domain.repository

import kotlinx.coroutines.flow.Flow
import ms.dev.jobtrackerpro.domain.model.ApplicationStatus
import ms.dev.jobtrackerpro.domain.model.StatusHistory

interface StatusHistoryRepository {
    
    fun getStatusHistory(applicationId: Long): Flow<List<StatusHistory>>
    
    fun getAllStatusHistory(): Flow<List<StatusHistory>>
    
    fun getStatusHistoryByDateRange(startDate: Long, endDate: Long): Flow<List<StatusHistory>>
    
    suspend fun getAverageTransitionTime(fromStatus: ApplicationStatus, toStatus: ApplicationStatus): Long?
    
    suspend fun insertStatusHistory(statusHistory: StatusHistory): Long
    
    suspend fun deleteByApplicationId(applicationId: Long)
    
    suspend fun deleteAll()
}
