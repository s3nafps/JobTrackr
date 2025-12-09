package ms.dev.jobtrackerpro.domain.repository

import kotlinx.coroutines.flow.Flow
import ms.dev.jobtrackerpro.domain.model.ApplicationStatus
import ms.dev.jobtrackerpro.domain.model.DashboardStatistics
import ms.dev.jobtrackerpro.domain.model.FilterState
import ms.dev.jobtrackerpro.domain.model.JobApplication
import ms.dev.jobtrackerpro.domain.model.SortOption

interface ApplicationRepository {
    
    fun getAllApplications(): Flow<List<JobApplication>>
    
    fun getApplicationById(id: Long): Flow<JobApplication?>
    
    fun getRecentApplications(limit: Int): Flow<List<JobApplication>>
    
    fun searchApplications(query: String): Flow<List<JobApplication>>
    
    fun getApplicationsByStatus(status: ApplicationStatus): Flow<List<JobApplication>>
    
    fun getApplicationsByDateRange(startDate: Long, endDate: Long): Flow<List<JobApplication>>
    
    fun getApplicationsSorted(sortOption: SortOption): Flow<List<JobApplication>>
    
    fun filterApplications(filter: FilterState): Flow<List<JobApplication>>
    
    fun getStatistics(): Flow<DashboardStatistics>
    
    suspend fun insertApplication(application: JobApplication): Long
    
    suspend fun updateApplication(application: JobApplication)
    
    suspend fun deleteApplication(id: Long)
    
    suspend fun deleteAllApplications()
}
