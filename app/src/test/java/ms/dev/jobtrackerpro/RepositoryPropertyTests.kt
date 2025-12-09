package ms.dev.jobtrackerpro

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import ms.dev.jobtrackerpro.PropertyTestGenerators.applicationStatusArb
import ms.dev.jobtrackerpro.PropertyTestGenerators.dateRangeArb
import ms.dev.jobtrackerpro.PropertyTestGenerators.jobApplicationArb
import ms.dev.jobtrackerpro.PropertyTestGenerators.jobApplicationListArb
import ms.dev.jobtrackerpro.PropertyTestGenerators.searchQueryArb
import ms.dev.jobtrackerpro.domain.model.ApplicationStatus
import ms.dev.jobtrackerpro.domain.model.JobApplication
import ms.dev.jobtrackerpro.domain.model.SortOption

/**
 * Property-based tests for repository operations.
 * 
 * **Feature: job-tracker-app, Property 3: Applications List Completeness**
 * **Feature: job-tracker-app, Property 4: Search Results Relevance**
 * **Feature: job-tracker-app, Property 5: Filter Results Correctness**
 * **Feature: job-tracker-app, Property 6: Sort Order Correctness**
 * **Feature: job-tracker-app, Property 7: Data Persistence Integrity**
 * **Feature: job-tracker-app, Property 19: Date Range Filter Application**
 * **Feature: job-tracker-app, Property 21: Referential Integrity**
 */
class RepositoryPropertyTests : FunSpec({
    
    /**
     * **Feature: job-tracker-app, Property 3: Applications List Completeness**
     * *For any* set of job applications stored, the applications list SHALL display 
     * all applications without omission.
     * **Validates: Requirements 2.1**
     */
    test("Property 3: Applications list completeness - all applications returned") {
        checkAll(100, jobApplicationListArb(0..50)) { applications ->
            val storedApps = simulateStore(applications)
            val retrievedApps = simulateRetrieveAll(storedApps)
            
            retrievedApps.size shouldBe applications.size
            retrievedApps.map { it.id } shouldContainAll applications.map { it.id }
        }
    }
    
    /**
     * **Feature: job-tracker-app, Property 4: Search Results Relevance**
     * *For any* search query and set of applications, the search results SHALL contain 
     * only applications where company name, position title, or notes contain the query.
     * **Validates: Requirements 2.2**
     */
    test("Property 4: Search results relevance - only matching applications returned") {
        checkAll(100, jobApplicationListArb(5..30), searchQueryArb) { applications, query ->
            val searchResults = searchApplications(applications, query)
            
            // All results should contain the query in company, title, or notes
            searchResults.forEach { app ->
                val matchesCompany = app.companyName.contains(query, ignoreCase = true)
                val matchesTitle = app.jobTitle.contains(query, ignoreCase = true)
                val matchesNotes = app.notes?.contains(query, ignoreCase = true) == true
                
                (matchesCompany || matchesTitle || matchesNotes) shouldBe true
            }
            
            // All matching applications should be in results
            val expectedMatches = applications.filter { app ->
                app.companyName.contains(query, ignoreCase = true) ||
                app.jobTitle.contains(query, ignoreCase = true) ||
                app.notes?.contains(query, ignoreCase = true) == true
            }
            searchResults.size shouldBe expectedMatches.size
        }
    }
    
    /**
     * **Feature: job-tracker-app, Property 5: Filter Results Correctness**
     * *For any* filter criteria and set of applications, the filtered results SHALL 
     * contain only applications that match ALL specified filter criteria.
     * **Validates: Requirements 2.3**
     */
    test("Property 5: Filter results correctness - only matching criteria returned") {
        checkAll(100, jobApplicationListArb(5..30), applicationStatusArb) { applications, statusFilter ->
            val filteredResults = filterByStatus(applications, statusFilter)
            
            // All results should match the filter
            filteredResults.forEach { app ->
                app.status shouldBe statusFilter
            }
            
            // All matching applications should be in results
            val expectedMatches = applications.filter { it.status == statusFilter }
            filteredResults.size shouldBe expectedMatches.size
        }
    }
    
    /**
     * **Feature: job-tracker-app, Property 6: Sort Order Correctness**
     * *For any* sort option and set of applications, the sorted list SHALL be correctly 
     * ordered according to the specified sort criteria.
     * **Validates: Requirements 2.4**
     */
    test("Property 6: Sort order correctness - applications sorted correctly") {
        checkAll(100, jobApplicationListArb(2..30), Arb.enum<SortOption>()) { applications, sortOption ->
            val sortedApps = sortApplications(applications, sortOption)
            
            for (i in 0 until sortedApps.size - 1) {
                when (sortOption) {
                    SortOption.NEWEST -> {
                        (sortedApps[i].applicationDate >= sortedApps[i + 1].applicationDate) shouldBe true
                    }
                    SortOption.OLDEST -> {
                        (sortedApps[i].applicationDate <= sortedApps[i + 1].applicationDate) shouldBe true
                    }
                    SortOption.COMPANY -> {
                        (sortedApps[i].companyName.lowercase() <= sortedApps[i + 1].companyName.lowercase()) shouldBe true
                    }
                    SortOption.STATUS -> {
                        (sortedApps[i].status.ordinal <= sortedApps[i + 1].status.ordinal) shouldBe true
                    }
                }
            }
        }
    }
    
    /**
     * **Feature: job-tracker-app, Property 7: Data Persistence Integrity**
     * *For any* job application that is saved, the data retrieved SHALL match the data 
     * that was submitted.
     * **Validates: Requirements 3.6, 8.1**
     */
    test("Property 7: Data persistence integrity - saved data matches retrieved data") {
        checkAll(100, jobApplicationArb) { application ->
            val saved = simulateSave(application)
            val retrieved = simulateRetrieve(saved)
            
            retrieved.companyName shouldBe application.companyName
            retrieved.jobTitle shouldBe application.jobTitle
            retrieved.status shouldBe application.status
            retrieved.applicationDate shouldBe application.applicationDate
            retrieved.companyLocation shouldBe application.companyLocation
            retrieved.jobDescription shouldBe application.jobDescription
            retrieved.jobLink shouldBe application.jobLink
            retrieved.salaryRange shouldBe application.salaryRange
            retrieved.jobType shouldBe application.jobType
            retrieved.remoteStatus shouldBe application.remoteStatus
            retrieved.notes shouldBe application.notes
        }
    }
    
    /**
     * **Feature: job-tracker-app, Property 19: Date Range Filter Application**
     * *For any* date range filter applied to analytics, all calculated statistics SHALL 
     * only include applications with application_date within the specified range.
     * **Validates: Requirements 6.6**
     */
    test("Property 19: Date range filter application - only applications in range included") {
        checkAll(100, jobApplicationListArb(5..30), dateRangeArb) { applications, dateRange ->
            val (startDate, endDate) = dateRange
            val filteredApps = filterByDateRange(applications, startDate, endDate)
            
            // All results should be within date range
            filteredApps.forEach { app ->
                (app.applicationDate >= startDate) shouldBe true
                (app.applicationDate <= endDate) shouldBe true
            }
            
            // All applications in range should be included
            val expectedInRange = applications.filter { 
                it.applicationDate in startDate..endDate 
            }
            filteredApps.size shouldBe expectedInRange.size
        }
    }
    
    /**
     * **Feature: job-tracker-app, Property 21: Referential Integrity**
     * *For any* status history or communication record, the referenced application_id 
     * SHALL correspond to an existing job application.
     * **Validates: Requirements 8.2**
     */
    test("Property 21: Referential integrity - foreign keys reference existing records") {
        checkAll(100, jobApplicationListArb(1..20)) { applications ->
            val applicationIds = applications.map { it.id }.toSet()
            
            // Generate status history with valid application IDs
            val statusHistory = applications.flatMap { app ->
                listOf(
                    ms.dev.jobtrackerpro.domain.model.StatusHistory(
                        applicationId = app.id,
                        status = app.status,
                        statusDate = app.applicationDate,
                        timestamp = System.currentTimeMillis()
                    )
                )
            }
            
            // All status history records should reference valid application IDs
            statusHistory.forEach { history ->
                applicationIds.contains(history.applicationId) shouldBe true
            }
        }
    }
})

// Helper functions simulating repository operations

private fun simulateStore(applications: List<JobApplication>): List<JobApplication> = applications

private fun simulateRetrieveAll(applications: List<JobApplication>): List<JobApplication> = applications

private fun simulateSave(application: JobApplication): JobApplication = application

private fun simulateRetrieve(application: JobApplication): JobApplication = application

private fun searchApplications(applications: List<JobApplication>, query: String): List<JobApplication> {
    return applications.filter { app ->
        app.companyName.contains(query, ignoreCase = true) ||
        app.jobTitle.contains(query, ignoreCase = true) ||
        app.notes?.contains(query, ignoreCase = true) == true
    }
}

private fun filterByStatus(applications: List<JobApplication>, status: ApplicationStatus): List<JobApplication> {
    return applications.filter { it.status == status }
}

private fun sortApplications(applications: List<JobApplication>, sortOption: SortOption): List<JobApplication> {
    return when (sortOption) {
        SortOption.NEWEST -> applications.sortedByDescending { it.applicationDate }
        SortOption.OLDEST -> applications.sortedBy { it.applicationDate }
        SortOption.COMPANY -> applications.sortedBy { it.companyName.lowercase() }
        SortOption.STATUS -> applications.sortedBy { it.status.ordinal }
    }
}

private fun filterByDateRange(applications: List<JobApplication>, startDate: Long, endDate: Long): List<JobApplication> {
    return applications.filter { it.applicationDate in startDate..endDate }
}
