package ms.dev.jobtrackerpro.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ms.dev.jobtrackerpro.data.local.dao.StatusHistoryDao
import ms.dev.jobtrackerpro.data.mapper.toEntity
import ms.dev.jobtrackerpro.data.mapper.toStatusHistoryDomainList
import ms.dev.jobtrackerpro.domain.model.ApplicationStatus
import ms.dev.jobtrackerpro.domain.model.StatusHistory
import ms.dev.jobtrackerpro.domain.repository.StatusHistoryRepository
import javax.inject.Inject

class StatusHistoryRepositoryImpl @Inject constructor(
    private val statusHistoryDao: StatusHistoryDao
) : StatusHistoryRepository {
    
    override fun getStatusHistory(applicationId: Long): Flow<List<StatusHistory>> {
        return statusHistoryDao.getStatusHistory(applicationId).map { it.toStatusHistoryDomainList() }
    }
    
    override fun getAllStatusHistory(): Flow<List<StatusHistory>> {
        return statusHistoryDao.getAllStatusHistory().map { it.toStatusHistoryDomainList() }
    }
    
    override fun getStatusHistoryByDateRange(startDate: Long, endDate: Long): Flow<List<StatusHistory>> {
        return statusHistoryDao.getStatusHistoryByDateRange(startDate, endDate)
            .map { it.toStatusHistoryDomainList() }
    }
    
    override suspend fun getAverageTransitionTime(
        fromStatus: ApplicationStatus, 
        toStatus: ApplicationStatus
    ): Long? {
        return statusHistoryDao.getAverageTransitionTime(fromStatus.name, toStatus.name)
    }
    
    override suspend fun insertStatusHistory(statusHistory: StatusHistory): Long {
        return statusHistoryDao.insert(statusHistory.toEntity())
    }
    
    override suspend fun deleteByApplicationId(applicationId: Long) {
        statusHistoryDao.deleteByApplicationId(applicationId)
    }
    
    override suspend fun deleteAll() {
        statusHistoryDao.deleteAll()
    }
}
