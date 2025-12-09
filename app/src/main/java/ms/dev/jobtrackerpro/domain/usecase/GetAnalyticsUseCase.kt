package ms.dev.jobtrackerpro.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import ms.dev.jobtrackerpro.domain.model.Analytics
import ms.dev.jobtrackerpro.domain.model.ApplicationStatus
import ms.dev.jobtrackerpro.domain.model.CompanyResponseRate
import ms.dev.jobtrackerpro.domain.model.DateRange
import ms.dev.jobtrackerpro.domain.model.MonthlyCount
import ms.dev.jobtrackerpro.domain.repository.ApplicationRepository
import ms.dev.jobtrackerpro.domain.repository.StatusHistoryRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

/**
 * Use case for getting comprehensive analytics data.
 */
class GetAnalyticsUseCase @Inject constructor(
    private val applicationRepository: ApplicationRepository,
    private val statusHistoryRepository: StatusHistoryRepository
) {
    operator fun invoke(dateRange: DateRange? = null): Flow<Analytics> {
        val applicationsFlow = if (dateRange != null) {
            applicationRepository.getApplicationsByDateRange(dateRange.startDate, dateRange.endDate)
        } else {
            applicationRepository.getAllApplications()
        }
        
        return applicationsFlow.map { applications ->
            val total = applications.size
            
            // Calculate response rate (applications that got any response)
            val responsesReceived = applications.count { app ->
                app.status != ApplicationStatus.APPLIED && app.status != ApplicationStatus.GHOSTED
            }
            val responseRate = if (total > 0) (responsesReceived.toFloat() / total) * 100 else 0f
            
            // Calculate interview rate (interviews / responses)
            val interviews = applications.count { it.status == ApplicationStatus.INTERVIEW }
            val interviewRate = if (responsesReceived > 0) (interviews.toFloat() / responsesReceived) * 100 else 0f
            
            // Calculate success rate (offers / total)
            val offers = applications.count { it.status == ApplicationStatus.OFFER }
            val successRate = if (total > 0) (offers.toFloat() / total) * 100 else 0f
            
            // Calculate status distribution
            val statusDistribution = applications.groupBy { it.status }
                .mapValues { it.value.size }
            
            // Calculate applications over time (by month)
            val applicationsOverTime = calculateMonthlyApplications(applications)
            
            // Calculate company response rates
            val companyResponseRates = calculateCompanyResponseRates(applications)
            
            Analytics(
                totalApplications = total,
                responseRate = responseRate,
                interviewRate = interviewRate,
                successRate = successRate,
                averageTimeToResponse = 0f, // Will be calculated from status history
                statusDistribution = statusDistribution,
                applicationsOverTime = applicationsOverTime,
                statusTransitionTimes = emptyMap(), // Will be calculated from status history
                companyResponseRates = companyResponseRates
            )
        }
    }
    
    private fun calculateMonthlyApplications(applications: List<ms.dev.jobtrackerpro.domain.model.JobApplication>): List<MonthlyCount> {
        val dateFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        
        return applications
            .groupBy { app ->
                calendar.timeInMillis = app.applicationDate
                val month = calendar.get(Calendar.MONTH)
                val year = calendar.get(Calendar.YEAR)
                Triple(dateFormat.format(calendar.time), year, month)
            }
            .map { (key, apps) ->
                MonthlyCount(
                    month = key.first,
                    year = key.second,
                    monthNumber = key.third,
                    count = apps.size
                )
            }
            .sortedWith(compareBy({ it.year }, { it.monthNumber }))
    }
    
    private fun calculateCompanyResponseRates(applications: List<ms.dev.jobtrackerpro.domain.model.JobApplication>): List<CompanyResponseRate> {
        return applications
            .groupBy { it.companyName }
            .map { (company, apps) ->
                val totalApps = apps.size
                val responses = apps.count { app ->
                    app.status != ApplicationStatus.APPLIED && app.status != ApplicationStatus.GHOSTED
                }
                CompanyResponseRate(
                    companyName = company,
                    totalApplications = totalApps,
                    responses = responses,
                    responseRate = if (totalApps > 0) (responses.toFloat() / totalApps) * 100 else 0f
                )
            }
            .sortedByDescending { it.responseRate }
            .take(5)
    }
}
