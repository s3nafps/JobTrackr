package ms.dev.jobtrackerpro.domain.model

/**
 * Dashboard statistics data class.
 */
data class DashboardStatistics(
    val totalApplications: Int = 0,
    val responsesReceived: Int = 0,
    val interviewsScheduled: Int = 0,
    val offersReceived: Int = 0,
    val rejections: Int = 0,
    val statusDistribution: Map<ApplicationStatus, Int> = emptyMap()
)

/**
 * Analytics data class for detailed insights.
 */
data class Analytics(
    val totalApplications: Int = 0,
    val responseRate: Float = 0f,
    val interviewRate: Float = 0f,
    val successRate: Float = 0f,
    val averageTimeToResponse: Float = 0f, // in days
    val statusDistribution: Map<ApplicationStatus, Int> = emptyMap(),
    val applicationsOverTime: List<MonthlyCount> = emptyList(),
    val statusTransitionTimes: Map<String, Float> = emptyMap(), // e.g., "APPLIED_TO_EMAIL" -> 5.2 days
    val companyResponseRates: List<CompanyResponseRate> = emptyList()
)

/**
 * Monthly application count for time-series chart.
 */
data class MonthlyCount(
    val month: String, // e.g., "Jan 2024"
    val year: Int,
    val monthNumber: Int,
    val count: Int
)

/**
 * Company response rate for analytics.
 */
data class CompanyResponseRate(
    val companyName: String,
    val totalApplications: Int,
    val responses: Int,
    val responseRate: Float
)

/**
 * Filter state for applications list.
 */
data class FilterState(
    val status: ApplicationStatus? = null,
    val startDate: Long? = null,
    val endDate: Long? = null,
    val company: String? = null,
    val jobType: JobType? = null,
    val remoteStatus: RemoteStatus? = null
)

/**
 * Date range for analytics filtering.
 */
data class DateRange(
    val startDate: Long,
    val endDate: Long
) {
    companion object {
        fun lastMonth(): DateRange {
            val now = System.currentTimeMillis()
            val thirtyDaysAgo = now - (30L * 24 * 60 * 60 * 1000)
            return DateRange(thirtyDaysAgo, now)
        }
        
        fun lastQuarter(): DateRange {
            val now = System.currentTimeMillis()
            val ninetyDaysAgo = now - (90L * 24 * 60 * 60 * 1000)
            return DateRange(ninetyDaysAgo, now)
        }
        
        fun lastYear(): DateRange {
            val now = System.currentTimeMillis()
            val oneYearAgo = now - (365L * 24 * 60 * 60 * 1000)
            return DateRange(oneYearAgo, now)
        }
    }
}
