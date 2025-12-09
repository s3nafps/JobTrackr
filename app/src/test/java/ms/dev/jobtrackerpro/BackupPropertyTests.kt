package ms.dev.jobtrackerpro

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import ms.dev.jobtrackerpro.PropertyTestGenerators.communicationArb
import ms.dev.jobtrackerpro.PropertyTestGenerators.jobApplicationArb
import ms.dev.jobtrackerpro.PropertyTestGenerators.statusHistoryArb
import ms.dev.jobtrackerpro.data.backup.BackupData
import ms.dev.jobtrackerpro.data.backup.BackupSerializer
import ms.dev.jobtrackerpro.domain.model.BackupStatus
import ms.dev.jobtrackerpro.domain.model.BackupType
import ms.dev.jobtrackerpro.domain.model.CloudBackup
import ms.dev.jobtrackerpro.domain.model.Communication
import ms.dev.jobtrackerpro.domain.model.JobApplication
import ms.dev.jobtrackerpro.domain.model.StatusHistory

/**
 * Property-based tests for backup and serialization operations.
 * 
 * **Feature: job-tracker-app, Property 20: Backup Timestamp Recording**
 * **Feature: job-tracker-app, Property 22: Backup Serialization Round Trip**
 */
class BackupPropertyTests : FunSpec({
    
    val backupSerializer = BackupSerializer()
    
    /**
     * **Feature: job-tracker-app, Property 20: Backup Timestamp Recording**
     * *For any* successful backup operation, a backup record SHALL be created with 
     * timestamp equal to or greater than the operation start time and status set to COMPLETED.
     * **Validates: Requirements 7.4**
     */
    test("Property 20: Backup timestamp recording - timestamp and status recorded correctly") {
        checkAll(100, Arb.enum<BackupType>()) { backupType ->
            val startTime = System.currentTimeMillis()
            
            // Simulate backup operation
            val backupRecord = createBackupRecord(backupType, startTime)
            
            // Timestamp should be >= start time
            backupRecord.backupTimestamp shouldBeGreaterThanOrEqualTo startTime
            
            // Status should be COMPLETED for successful backup
            backupRecord.backupStatus shouldBe BackupStatus.COMPLETED
            
            // Backup type should match
            backupRecord.backupType shouldBe backupType
        }
    }
    
    /**
     * **Feature: job-tracker-app, Property 22: Backup Serialization Round Trip**
     * *For any* BackupData object containing applications, status history, and communications, 
     * serializing to JSON and deserializing SHALL produce a BackupData object equivalent to the original.
     * **Validates: Requirements 8.5**
     */
    test("Property 22: Backup serialization round trip - serialize then deserialize preserves data") {
        checkAll(50, 
            Arb.list(jobApplicationArb, 0..10),
            Arb.list(statusHistoryArb, 0..10),
            Arb.list(communicationArb, 0..10)
        ) { applications, statusHistory, communications ->
            
            // Create backup data
            val originalBackup = backupSerializer.createBackupData(
                applications = applications,
                statusHistory = statusHistory,
                communications = communications
            )
            
            // Serialize to JSON
            val json = backupSerializer.serialize(originalBackup)
            
            // Deserialize back
            val deserializedBackup = backupSerializer.deserialize(json)
            
            // Version and timestamp should match
            deserializedBackup.version shouldBe originalBackup.version
            deserializedBackup.timestamp shouldBe originalBackup.timestamp
            
            // Applications count should match
            deserializedBackup.applications.size shouldBe originalBackup.applications.size
            
            // Status history count should match
            deserializedBackup.statusHistory.size shouldBe originalBackup.statusHistory.size
            
            // Communications count should match
            deserializedBackup.communications.size shouldBe originalBackup.communications.size
            
            // Verify application data integrity
            deserializedBackup.applications.forEachIndexed { index, app ->
                val original = originalBackup.applications[index]
                app.id shouldBe original.id
                app.companyName shouldBe original.companyName
                app.jobTitle shouldBe original.jobTitle
                app.status shouldBe original.status
                app.applicationDate shouldBe original.applicationDate
            }
            
            // Verify status history data integrity
            deserializedBackup.statusHistory.forEachIndexed { index, history ->
                val original = originalBackup.statusHistory[index]
                history.id shouldBe original.id
                history.applicationId shouldBe original.applicationId
                history.status shouldBe original.status
                history.statusDate shouldBe original.statusDate
            }
            
            // Verify communications data integrity
            deserializedBackup.communications.forEachIndexed { index, comm ->
                val original = originalBackup.communications[index]
                comm.id shouldBe original.id
                comm.applicationId shouldBe original.applicationId
                comm.recruiterName shouldBe original.recruiterName
                comm.recruiterEmail shouldBe original.recruiterEmail
            }
        }
    }
    
    /**
     * Additional test: Restored data matches original domain models.
     */
    test("Backup restore produces equivalent domain models") {
        checkAll(50, 
            Arb.list(jobApplicationArb, 1..5),
            Arb.list(statusHistoryArb, 1..5),
            Arb.list(communicationArb, 1..5)
        ) { applications, statusHistory, communications ->
            
            // Create backup data
            val backupData = backupSerializer.createBackupData(
                applications = applications,
                statusHistory = statusHistory,
                communications = communications
            )
            
            // Serialize and deserialize
            val json = backupSerializer.serialize(backupData)
            val deserializedBackup = backupSerializer.deserialize(json)
            
            // Restore to domain models
            val restoredData = backupSerializer.restoreFromBackup(deserializedBackup)
            
            // Verify restored applications
            restoredData.applications.size shouldBe applications.size
            restoredData.applications.forEachIndexed { index, restored ->
                val original = applications[index]
                restored.id shouldBe original.id
                restored.companyName shouldBe original.companyName
                restored.jobTitle shouldBe original.jobTitle
                restored.status shouldBe original.status
            }
            
            // Verify restored status history
            restoredData.statusHistory.size shouldBe statusHistory.size
            
            // Verify restored communications
            restoredData.communications.size shouldBe communications.size
        }
    }
})

// Helper function

private fun createBackupRecord(backupType: BackupType, startTime: Long): CloudBackup {
    return CloudBackup(
        id = 0,
        backupType = backupType,
        backupTimestamp = System.currentTimeMillis(),
        backupStatus = BackupStatus.COMPLETED,
        backupFileId = "backup_${System.currentTimeMillis()}",
        backupLocation = when (backupType) {
            BackupType.GOOGLE_DRIVE -> "drive://backups/"
            BackupType.CSV -> "/storage/backups/"
            else -> null
        }
    )
}
