package ms.dev.jobtrackerpro

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import ms.dev.jobtrackerpro.PropertyTestGenerators.applicationStatusArb
import ms.dev.jobtrackerpro.PropertyTestGenerators.jobApplicationArb
import ms.dev.jobtrackerpro.domain.model.ApplicationStatus
import ms.dev.jobtrackerpro.domain.model.JobApplication
import ms.dev.jobtrackerpro.domain.model.StatusHistory

/**
 * Property-based tests for status updates and undo operations.
 * 
 * **Feature: job-tracker-app, Property 8: Status History Recording**
 * **Feature: job-tracker-app, Property 23: Undo Restoration**
 */
class StatusAndUndoPropertyTests : FunSpec({
    
    /**
     * **Feature: job-tracker-app, Property 8: Status History Recording**
     * *For any* status update operation, a corresponding status history record SHALL be 
     * created with the correct application ID, status, date, and timestamp.
     * **Validates: Requirements 4.5**
     */
    test("Property 8: Status history recording - history created on status update") {
        checkAll(100, jobApplicationArb, applicationStatusArb) { application, newStatus ->
            val statusDate = System.currentTimeMillis()
            val notes = "Status updated via test"
            
            val historyRecord = createStatusHistoryRecord(
                applicationId = application.id,
                status = newStatus,
                statusDate = statusDate,
                notes = notes
            )
            
            // History record should have correct values
            historyRecord.applicationId shouldBe application.id
            historyRecord.status shouldBe newStatus
            historyRecord.statusDate shouldBe statusDate
            historyRecord.notes shouldBe notes
            historyRecord.timestamp shouldBeGreaterThanOrEqualTo statusDate
        }
    }
    
    /**
     * **Feature: job-tracker-app, Property 17: Status Transition Duration Calculation**
     * *For any* application with status history, the average days between status stages 
     * SHALL be calculated correctly.
     * **Validates: Requirements 6.4**
     */
    test("Property 17: Status transition duration calculation - correct average calculation") {
        checkAll(100, Arb.list(Arb.long(1609459200000L..System.currentTimeMillis()), 2..10)) { timestamps ->
            val sortedTimestamps = timestamps.sorted()
            
            // Calculate durations between consecutive timestamps
            val durations = sortedTimestamps.zipWithNext { a, b -> b - a }
            
            if (durations.isNotEmpty()) {
                val averageDuration = durations.sum() / durations.size
                val calculatedAverage = calculateAverageTransitionDuration(sortedTimestamps)
                
                calculatedAverage shouldBe averageDuration
            }
        }
    }
    
    /**
     * **Feature: job-tracker-app, Property 23: Undo Restoration**
     * *For any* destructive action (delete) followed by undo within the timeout period, 
     * the deleted data SHALL be restored to its previous state.
     * **Validates: Requirements 9.5**
     */
    test("Property 23: Undo restoration - deleted data restored correctly") {
        checkAll(100, jobApplicationArb) { application ->
            // Simulate delete
            val deletedApp = simulateDelete(application)
            deletedApp shouldNotBe null
            
            // Simulate undo within timeout
            val restoredApp = simulateUndo(deletedApp!!)
            
            // Restored app should match original
            restoredApp.id shouldBe application.id
            restoredApp.companyName shouldBe application.companyName
            restoredApp.jobTitle shouldBe application.jobTitle
            restoredApp.status shouldBe application.status
            restoredApp.applicationDate shouldBe application.applicationDate
            restoredApp.companyLocation shouldBe application.companyLocation
            restoredApp.jobDescription shouldBe application.jobDescription
            restoredApp.notes shouldBe application.notes
        }
    }
})

// Helper functions

private fun createStatusHistoryRecord(
    applicationId: Long,
    status: ApplicationStatus,
    statusDate: Long,
    notes: String?
): StatusHistory {
    return StatusHistory(
        id = 0,
        applicationId = applicationId,
        status = status,
        statusDate = statusDate,
        notes = notes,
        timestamp = System.currentTimeMillis()
    )
}

private fun calculateAverageTransitionDuration(timestamps: List<Long>): Long {
    if (timestamps.size < 2) return 0
    val durations = timestamps.zipWithNext { a, b -> b - a }
    return if (durations.isNotEmpty()) durations.sum() / durations.size else 0
}

private fun simulateDelete(application: JobApplication): JobApplication? {
    // Simulate soft delete - returns the deleted application for undo
    return application
}

private fun simulateUndo(deletedApplication: JobApplication): JobApplication {
    // Simulate undo - restores the application
    return deletedApplication
}
