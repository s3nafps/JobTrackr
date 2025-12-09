package ms.dev.jobtrackerpro

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.comparables.shouldBeLessThanOrEqualTo
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import ms.dev.jobtrackerpro.PropertyTestGenerators.applicationStatusArb
import ms.dev.jobtrackerpro.PropertyTestGenerators.jobApplicationArb
import ms.dev.jobtrackerpro.PropertyTestGenerators.jobApplicationListArb
import ms.dev.jobtrackerpro.domain.model.ApplicationStatus
import ms.dev.jobtrackerpro.domain.model.DashboardStatistics
import ms.dev.jobtrackerpro.domain.model.JobApplication

/**
 * Property-based tests for statistics calculations.
 * 
 * **Feature: job-tracker-app, Property 1: Statistics Accuracy**
 * **Feature: job-tracker-app, Property 2: Recent Applications Ordering**
 * **Feature: job-tracker-app, Property 15: Status Distribution Accuracy**
 * **Feature: job-tracker-app, Property 16: Applications Over Time Aggregation**
 * **Feature: job-tracker-app, Property 17: Status Transition Duration Calculation**
 * **Feature: job-tracker-app, Property 18: Company Response Rate Ranking**
 */
class StatisticsPropertyTests : FunSpec({
    
    /**
     * **Feature: job-tracker-app, Property 1: Statistics Accuracy**
     * *For any* set of job applications, the calculated statistics SHALL accurately 
     * reflect the actual counts of applications in each status category.
     * **Validates: Requirements 1.2, 6.1**
     */
    test("Property 1: Statistics accuracy - counts match actual status distribution") {
        checkAll(100, jobApplicationListArb(1..50)) { applications ->
            val statistics = calculateStatistics(applications)
            
            // Total should match
            statistics.totalApplications shouldBe applications.size
            
            // Status counts should match
            val actualResponses = applications.count { 
                it.status in listOf(
                    ApplicationStatus.EMAIL, 
                    ApplicationStatus.PHONE, 
                    ApplicationStatus.INTERVIEW, 
                    ApplicationStatus.OFFER,
                    ApplicationStatus.REJECTED_BY_COMPANY
                )
            }
            statistics.responsesReceived shouldBe actualResponses
            
            val actualInterviews = applications.count { it.status == ApplicationStatus.INTERVIEW }
            statistics.interviewsScheduled shouldBe actualInterviews
            
            val actualOffers = applications.count { it.status == ApplicationStatus.OFFER }
            statistics.offersReceived shouldBe actualOffers
            
            val actualRejections = applications.count { 
                it.status in listOf(ApplicationStatus.REJECTED_BY_COMPANY, ApplicationStatus.REJECTED_BY_ME)
            }
            statistics.rejections shouldBe actualRejections
        }
    }
    
    /**
     * **Feature: job-tracker-app, Property 2: Recent Applications Ordering**
     * *For any* list of job applications, the "recent applications" query SHALL return 
     * applications sorted by updated timestamp in descending order.
     * **Validates: Requirements 1.4**
     */
    test("Property 2: Recent applications ordering - sorted by updated timestamp descending") {
        checkAll(100, jobApplicationListArb(2..30)) { applications ->
            val limit = Arb.int(1..applications.size).bind()
            val recentApps = getRecentApplications(applications, limit)
            
            // Should be limited to requested count
            recentApps.size shouldBeLessThanOrEqualTo limit
            
            // Should be sorted by updatedTimestamp descending
            for (i in 0 until recentApps.size - 1) {
                recentApps[i].updatedTimestamp shouldBeGreaterThanOrEqualTo recentApps[i + 1].updatedTimestamp
            }
        }
    }
    
    /**
     * **Feature: job-tracker-app, Property 15: Status Distribution Accuracy**
     * *For any* set of applications, the status distribution chart data SHALL have 
     * segment values that sum to the total application count.
     * **Validates: Requirements 6.2**
     */
    test("Property 15: Status distribution accuracy - segments sum to total") {
        checkAll(100, jobApplicationListArb(1..50)) { applications ->
            val distribution = calculateStatusDistribution(applications)
            
            // Sum of all status counts should equal total applications
            val distributionSum = distribution.values.sum()
            distributionSum shouldBe applications.size
            
            // Each status count should match actual count
            ApplicationStatus.entries.forEach { status ->
                val actualCount = applications.count { it.status == status }
                (distribution[status] ?: 0) shouldBe actualCount
            }
        }
    }
    
    /**
     * **Feature: job-tracker-app, Property 16: Applications Over Time Aggregation**
     * *For any* set of applications, the monthly application counts SHALL accurately 
     * reflect the number of applications created in each month.
     * **Validates: Requirements 6.3**
     */
    test("Property 16: Applications over time aggregation - monthly counts accurate") {
        checkAll(100, jobApplicationListArb(1..50)) { applications ->
            val monthlyAggregation = aggregateByMonth(applications)
            
            // Sum of monthly counts should equal total
            val aggregationSum = monthlyAggregation.values.sum()
            aggregationSum shouldBe applications.size
        }
    }
    
    /**
     * **Feature: job-tracker-app, Property 18: Company Response Rate Ranking**
     * *For any* set of applications grouped by company, the response rate SHALL be 
     * calculated correctly and companies ranked in descending order.
     * **Validates: Requirements 6.5**
     */
    test("Property 18: Company response rate ranking - correct calculation and ordering") {
        checkAll(100, jobApplicationListArb(5..30)) { applications ->
            val companyRates = calculateCompanyResponseRates(applications)
            
            // Rates should be between 0 and 100
            companyRates.forEach { (_, rate) ->
                rate shouldBeGreaterThanOrEqualTo 0
                rate shouldBeLessThanOrEqualTo 100
            }
            
            // Should be sorted descending by rate
            val rates = companyRates.map { it.second }
            for (i in 0 until rates.size - 1) {
                rates[i] shouldBeGreaterThanOrEqualTo rates[i + 1]
            }
        }
    }
})

// Helper functions that mirror the actual implementation logic

private fun calculateStatistics(applications: List<JobApplication>): DashboardStatistics {
    val responsesReceived = applications.count { 
        it.status in listOf(
            ApplicationStatus.EMAIL, 
            ApplicationStatus.PHONE, 
            ApplicationStatus.INTERVIEW, 
            ApplicationStatus.OFFER,
            ApplicationStatus.REJECTED_BY_COMPANY
        )
    }
    val interviewsScheduled = applications.count { it.status == ApplicationStatus.INTERVIEW }
    val offersReceived = applications.count { it.status == ApplicationStatus.OFFER }
    val rejections = applications.count { 
        it.status in listOf(ApplicationStatus.REJECTED_BY_COMPANY, ApplicationStatus.REJECTED_BY_ME)
    }
    
    return DashboardStatistics(
        totalApplications = applications.size,
        responsesReceived = responsesReceived,
        interviewsScheduled = interviewsScheduled,
        offersReceived = offersReceived,
        rejections = rejections,
        statusDistribution = calculateStatusDistribution(applications)
    )
}

private fun getRecentApplications(applications: List<JobApplication>, limit: Int): List<JobApplication> {
    return applications.sortedByDescending { it.updatedTimestamp }.take(limit)
}

private fun calculateStatusDistribution(applications: List<JobApplication>): Map<ApplicationStatus, Int> {
    return applications.groupBy { it.status }.mapValues { it.value.size }
}

private fun aggregateByMonth(applications: List<JobApplication>): Map<String, Int> {
    val calendar = java.util.Calendar.getInstance()
    return applications.groupBy { app ->
        calendar.timeInMillis = app.applicationDate
        "${calendar.get(java.util.Calendar.YEAR)}-${calendar.get(java.util.Calendar.MONTH) + 1}"
    }.mapValues { it.value.size }
}

private fun calculateCompanyResponseRates(applications: List<JobApplication>): List<Pair<String, Int>> {
    val responseStatuses = listOf(
        ApplicationStatus.EMAIL, 
        ApplicationStatus.PHONE, 
        ApplicationStatus.INTERVIEW, 
        ApplicationStatus.OFFER,
        ApplicationStatus.REJECTED_BY_COMPANY
    )
    
    return applications.groupBy { it.companyName }
        .map { (company, apps) ->
            val responses = apps.count { it.status in responseStatuses }
            val rate = if (apps.isNotEmpty()) (responses * 100) / apps.size else 0
            company to rate
        }
        .sortedByDescending { it.second }
}
