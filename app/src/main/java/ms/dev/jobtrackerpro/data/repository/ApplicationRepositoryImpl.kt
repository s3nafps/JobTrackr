package ms.dev.jobtrackerpro.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ms.dev.jobtrackerpro.data.local.dao.JobApplicationDao
import ms.dev.jobtrackerpro.data.mapper.toDomain
import ms.dev.jobtrackerpro.data.mapper.toDomainList
import ms.dev.jobtrackerpro.data.mapper.toEntity
import ms.dev.jobtrackerpro.domain.model.ApplicationStatus
import ms.dev.jobtrackerpro.domain.model.DashboardStatistics
import ms.dev.jobtrackerpro.domain.model.FilterState
import ms.dev.jobtrackerpro.domain.model.JobApplication
import ms.dev.jobtrackerpro.domain.model.SortOption
import ms.dev.jobtrackerpro.domain.repository.ApplicationRepository
import javax.inject.Inject

class ApplicationRepositoryImpl @Inject constructor(
    private val jobApplicationDao: JobApplicationDao
) : ApplicationRepository {
    
    override fun getAllApplications(): Flow<List<JobApplication>> {
        return jobApplicationDao.getAllApplications().map { it.toDomainList() }
    }
    
    override fun getApplicationById(id: Long): Flow<JobApplication?> {
        return jobApplicationDao.getApplicationById(id).map { it?.toDomain() }
    }
    
    override fun getRecentApplications(limit: Int): Flow<List<JobApplication>> {
        return jobApplicationDao.getRecentApplications(limit).map { it.toDomainList() }
    }
    
    override fun searchApplications(query: String): Flow<List<JobApplication>> {
        return jobApplicationDao.searchApplications(query).map { it.toDomainList() }
    }
    
    override fun getApplicationsByStatus(status: ApplicationStatus): Flow<List<JobApplication>> {
        return jobApplicationDao.getApplicationsByStatus(status.name).map { it.toDomainList() }
    }
    
    override fun getApplicationsByDateRange(startDate: Long, endDate: Long): Flow<List<JobApplication>> {
        return jobApplicationDao.getApplicationsByDateRange(startDate, endDate).map { it.toDomainList() }
    }
    
    override fun getApplicationsSorted(sortOption: SortOption): Flow<List<JobApplication>> {
        return when (sortOption) {
            SortOption.NEWEST -> jobApplicationDao.getApplicationsSortedByNewest()
            SortOption.OLDEST -> jobApplicationDao.getApplicationsSortedByOldest()
            SortOption.COMPANY -> jobApplicationDao.getApplicationsSortedByCompany()
            SortOption.STATUS -> jobApplicationDao.getApplicationsSortedByStatus()
        }.map { it.toDomainList() }
    }
    
    // OPTIMIZATION: Use early returns for faster filtering
    override fun filterApplications(filter: FilterState): Flow<List<JobApplication>> {
        return getAllApplications().map { applications ->
            // OPTIMIZATION: Pre-compute filter conditions once
            val hasStatusFilter = filter.status != null
            val hasDateFilter = filter.startDate != null && filter.endDate != null
            val hasCompanyFilter = !filter.company.isNullOrBlank()
            val companyLower = filter.company?.lowercase()
            val hasJobTypeFilter = filter.jobType != null
            val hasRemoteFilter = filter.remoteStatus != null
            
            // Early return if no filters
            if (!hasStatusFilter && !hasDateFilter && !hasCompanyFilter && !hasJobTypeFilter && !hasRemoteFilter) {
                return@map applications
            }
            
            applications.filter { app ->
                // OPTIMIZATION: Check cheapest conditions first (enum comparisons)
                if (hasStatusFilter && app.status != filter.status) return@filter false
                if (hasJobTypeFilter && app.jobType != filter.jobType) return@filter false
                if (hasRemoteFilter && app.remoteStatus != filter.remoteStatus) return@filter false
                
                if (hasDateFilter && app.applicationDate !in filter.startDate!!..filter.endDate!!) {
                    return@filter false
                }
                
                if (hasCompanyFilter && !app.companyName.lowercase().contains(companyLower!!)) {
                    return@filter false
                }
                
                true
            }
        }
    }
    
    override fun getStatistics(): Flow<DashboardStatistics> {
        return jobApplicationDao.getStatusDistribution().map { statusCounts ->
            val distribution = statusCounts.associate { 
                ApplicationStatus.fromString(it.status) to it.count 
            }
            
            val total = distribution.values.sum()
            val responses = distribution.filterKeys { 
                it != ApplicationStatus.APPLIED && it != ApplicationStatus.GHOSTED 
            }.values.sum()
            val interviews = distribution[ApplicationStatus.INTERVIEW] ?: 0
            val offers = distribution[ApplicationStatus.OFFER] ?: 0
            val rejections = (distribution[ApplicationStatus.REJECTED_BY_COMPANY] ?: 0) +
                           (distribution[ApplicationStatus.REJECTED_BY_ME] ?: 0)
            
            DashboardStatistics(
                totalApplications = total,
                responsesReceived = responses,
                interviewsScheduled = interviews,
                offersReceived = offers,
                rejections = rejections,
                statusDistribution = distribution
            )
        }
    }
    
    override suspend fun insertApplication(application: JobApplication): Long {
        val entity = application.copy(
            createdTimestamp = System.currentTimeMillis(),
            updatedTimestamp = System.currentTimeMillis()
        ).toEntity()
        return jobApplicationDao.insert(entity)
    }
    
    override suspend fun updateApplication(application: JobApplication) {
        val entity = application.copy(
            updatedTimestamp = System.currentTimeMillis()
        ).toEntity()
        jobApplicationDao.update(entity)
    }
    
    override suspend fun deleteApplication(id: Long) {
        jobApplicationDao.deleteById(id)
    }
    
    override suspend fun deleteAllApplications() {
        jobApplicationDao.deleteAll()
    }
}
