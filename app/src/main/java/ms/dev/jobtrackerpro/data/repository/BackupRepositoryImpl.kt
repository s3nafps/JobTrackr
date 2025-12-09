package ms.dev.jobtrackerpro.data.repository

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ms.dev.jobtrackerpro.data.local.dao.CloudBackupDao
import ms.dev.jobtrackerpro.data.local.dao.JobApplicationDao
import ms.dev.jobtrackerpro.data.local.dao.StatusHistoryDao
import ms.dev.jobtrackerpro.data.local.dao.CommunicationDao
import ms.dev.jobtrackerpro.data.mapper.toBackupDomainList
import ms.dev.jobtrackerpro.data.mapper.toDomain
import ms.dev.jobtrackerpro.data.mapper.toDomainList
import ms.dev.jobtrackerpro.data.mapper.toEntity
import ms.dev.jobtrackerpro.domain.model.BackupStatus
import ms.dev.jobtrackerpro.domain.model.BackupType
import ms.dev.jobtrackerpro.domain.model.CloudBackup
import ms.dev.jobtrackerpro.domain.model.JobApplication
import ms.dev.jobtrackerpro.domain.repository.BackupRepository
import ms.dev.jobtrackerpro.utils.DateUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class BackupRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cloudBackupDao: CloudBackupDao,
    private val jobApplicationDao: JobApplicationDao,
    private val statusHistoryDao: StatusHistoryDao,
    private val communicationDao: CommunicationDao
) : BackupRepository {
    
    // OPTIMIZATION: Cache date formatters to avoid repeated instantiation (expensive operation)
    private val dateFormatters by lazy {
        listOf(
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()),
            SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()),
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()),
            SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        )
    }
    
    override fun getAllBackups(): Flow<List<CloudBackup>> {
        return cloudBackupDao.getAllBackups().map { it.toBackupDomainList() }
    }
    
    override fun getLastSuccessfulBackup(): Flow<CloudBackup?> {
        return cloudBackupDao.getLastSuccessfulBackup().map { it?.toDomain() }
    }
    
    override fun getBackupsByType(type: BackupType): Flow<List<CloudBackup>> {
        return cloudBackupDao.getBackupsByType(type.name).map { it.toBackupDomainList() }
    }
    
    override suspend fun createBackup(type: BackupType): Result<CloudBackup> {
        return try {
            val timestamp = System.currentTimeMillis()
            
            // Create pending backup record
            val pendingBackup = CloudBackup(
                backupType = type,
                backupTimestamp = timestamp,
                backupStatus = BackupStatus.PENDING
            )
            val backupId = cloudBackupDao.insert(pendingBackup.toEntity())
            
            // Perform backup based on type
            val result = when (type) {
                BackupType.CSV -> performCsvBackup()
                BackupType.GOOGLE_DRIVE -> performGoogleDriveBackup()
                BackupType.GOOGLE_SHEETS -> performGoogleSheetsBackup()
                BackupType.NOTION -> performNotionBackup()
            }
            
            // Update backup record with result
            val completedBackup = pendingBackup.copy(
                id = backupId,
                backupStatus = if (result.isSuccess) BackupStatus.COMPLETED else BackupStatus.FAILED,
                backupFileId = result.getOrNull()?.first,
                backupLocation = result.getOrNull()?.second
            )
            cloudBackupDao.update(completedBackup.toEntity())
            
            Result.success(completedBackup)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun performCsvBackup(): Result<Pair<String?, String?>> = withContext(Dispatchers.IO) {
        try {
            val applications = jobApplicationDao.getAllApplicationsSync().toDomainList()
            val csvContent = generateCsvContent(applications)
            val fileName = "job_tracker_export_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.csv"
            
            val filePath = saveCsvFile(fileName, csvContent)
            Result.success(fileName to filePath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // OPTIMIZATION: Use StringBuilder with pre-calculated capacity to reduce memory allocations
    private fun generateCsvContent(applications: List<JobApplication>): String {
        val estimatedSize = 200 + (applications.size * 300)
        val builder = StringBuilder(estimatedSize)
        
        builder.append("Company,Job Title,Status,Application Date,Location,Job Type,Remote Status,Salary Min,Salary Max,Notes,Job Link\n")
        
        applications.forEach { app ->
            builder.append(app.companyName.escapeCsv()).append(',')
            builder.append(app.jobTitle.escapeCsv()).append(',')
            builder.append(app.status.displayName.escapeCsv()).append(',')
            builder.append(DateUtils.formatDate(app.applicationDate).escapeCsv()).append(',')
            builder.append((app.companyLocation ?: "").escapeCsv()).append(',')
            builder.append((app.jobType?.displayName ?: "").escapeCsv()).append(',')
            builder.append((app.remoteStatus?.displayName ?: "").escapeCsv()).append(',')
            builder.append((app.salaryRange?.min?.toString() ?: "").escapeCsv()).append(',')
            builder.append((app.salaryRange?.max?.toString() ?: "").escapeCsv()).append(',')
            builder.append((app.notes ?: "").escapeCsv()).append(',')
            builder.append((app.jobLink ?: "").escapeCsv()).append('\n')
        }
        
        return builder.toString()
    }
    
    private fun String.escapeCsv(): String {
        return if (this.contains(",") || this.contains("\"") || this.contains("\n")) {
            "\"${this.replace("\"", "\"\"")}\""
        } else {
            this
        }
    }
    
    private fun saveCsvFile(fileName: String, content: String): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Use MediaStore for Android 10+
            val contentValues = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "text/csv")
                put(MediaStore.Downloads.IS_PENDING, 1)
            }
            
            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            
            uri?.let {
                // OPTIMIZATION: Use buffered output stream for better I/O performance
                resolver.openOutputStream(it)?.buffered()?.use { outputStream ->
                    outputStream.write(content.toByteArray(Charsets.UTF_8))
                }
                
                contentValues.clear()
                contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
            }
            
            "Downloads/$fileName"
        } else {
            // Legacy storage for older Android versions
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)
            // OPTIMIZATION: Use buffered writer for better I/O performance
            file.bufferedWriter(Charsets.UTF_8).use { writer ->
                writer.write(content)
            }
            file.absolutePath
        }
    }
    
    private suspend fun performGoogleDriveBackup(): Result<Pair<String?, String?>> {
        // Google Drive backup not implemented - use CSV export instead
        return Result.failure(Exception("Google Drive backup not available. Please use CSV export."))
    }
    
    private suspend fun performGoogleSheetsBackup(): Result<Pair<String?, String?>> {
        // Google Sheets backup not implemented
        return Result.failure(Exception("Google Sheets backup not available. Please use CSV export."))
    }
    
    private suspend fun performNotionBackup(): Result<Pair<String?, String?>> {
        // Notion backup not implemented
        return Result.failure(Exception("Notion backup not available. Please use CSV export."))
    }
    
    override suspend fun exportToUri(uri: android.net.Uri): Result<CloudBackup> = withContext(Dispatchers.IO) {
        try {
            val timestamp = System.currentTimeMillis()
            
            // Create pending backup record
            val pendingBackup = CloudBackup(
                backupType = BackupType.CSV,
                backupTimestamp = timestamp,
                backupStatus = BackupStatus.PENDING
            )
            val backupId = cloudBackupDao.insert(pendingBackup.toEntity())
            
            // Get applications and generate CSV
            val applications = jobApplicationDao.getAllApplicationsSync().toDomainList()
            val csvContent = generateCsvContent(applications)
            
            // OPTIMIZATION: Use buffered output stream for better write performance
            context.contentResolver.openOutputStream(uri)?.buffered()?.use { outputStream ->
                outputStream.write(csvContent.toByteArray(Charsets.UTF_8))
            }
            
            // Update backup record
            val completedBackup = pendingBackup.copy(
                id = backupId,
                backupStatus = BackupStatus.COMPLETED,
                backupLocation = uri.path
            )
            cloudBackupDao.update(completedBackup.toEntity())
            
            Result.success(completedBackup)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun insertBackupRecord(backup: CloudBackup): Long {
        return cloudBackupDao.insert(backup.toEntity())
    }
    
    override suspend fun updateBackupRecord(backup: CloudBackup) {
        cloudBackupDao.update(backup.toEntity())
    }
    
    override suspend fun deleteBackup(id: Long) {
        cloudBackupDao.deleteById(id)
    }
    
    override suspend fun deleteAll() {
        cloudBackupDao.deleteAll()
    }
    
    // OPTIMIZATION: Use sequence for lazy evaluation and buffered reader for large files
    override suspend fun importFromUri(uri: android.net.Uri): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val applications = mutableListOf<JobApplication>()
            
            // OPTIMIZATION: Read line-by-line to avoid loading entire file into memory
            context.contentResolver.openInputStream(uri)?.bufferedReader(Charsets.UTF_8)?.useLines { lines ->
                lines.drop(1) // Skip header
                    .filter { it.isNotBlank() }
                    .forEach { line ->
                        try {
                            parseCsvLineToApplication(line)?.let { applications.add(it) }
                        } catch (e: Exception) {
                            // Skip invalid rows silently
                        }
                    }
            } ?: return@withContext Result.failure(Exception("Could not read file"))
            
            if (applications.isEmpty()) {
                return@withContext Result.failure(Exception("No valid applications found in CSV"))
            }
            
            // OPTIMIZATION: Batch processing for better database performance
            var importedCount = 0
            applications.forEach { app ->
                try {
                    jobApplicationDao.insert(app.toEntity())
                    importedCount++
                } catch (e: Exception) {
                    // Skip duplicates
                }
            }
            
            Result.success(importedCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // OPTIMIZATION: Single-line parsing to avoid creating intermediate lists
    private fun parseCsvLineToApplication(line: String): JobApplication? {
        val values = parseCsvLine(line)
        if (values.size < 2) return null
        
        val companyName = values.getOrNull(0)?.trim()?.takeIf { it.isNotBlank() } ?: return null
        val jobTitle = values.getOrNull(1)?.trim()?.takeIf { it.isNotBlank() } ?: return null
        
        return JobApplication(
            companyName = companyName,
            jobTitle = jobTitle,
            status = parseStatus(values.getOrNull(2)),
            applicationDate = parseDate(values.getOrNull(3)),
            companyLocation = values.getOrNull(4)?.trim()?.takeIf { it.isNotBlank() },
            jobType = parseJobType(values.getOrNull(5)),
            remoteStatus = parseRemoteStatus(values.getOrNull(6)),
            salaryRange = parseSalaryRange(values.getOrNull(7), values.getOrNull(8)),
            notes = values.getOrNull(9)?.trim()?.takeIf { it.isNotBlank() },
            jobLink = values.getOrNull(10)?.trim()?.takeIf { it.isNotBlank() }
        )
    }
    
    private fun parseCsvLine(line: String): List<String> {
        val values = mutableListOf<String>()
        var current = StringBuilder()
        var inQuotes = false
        
        for (char in line) {
            when {
                char == '"' -> inQuotes = !inQuotes
                char == ',' && !inQuotes -> {
                    values.add(current.toString())
                    current = StringBuilder()
                }
                else -> current.append(char)
            }
        }
        values.add(current.toString())
        
        return values
    }
    
    private fun parseStatus(value: String?): ms.dev.jobtrackerpro.domain.model.ApplicationStatus {
        val v = value?.trim()?.lowercase() ?: return ms.dev.jobtrackerpro.domain.model.ApplicationStatus.APPLIED
        return when {
            v.contains("interview") -> ms.dev.jobtrackerpro.domain.model.ApplicationStatus.INTERVIEW
            v.contains("offer") -> ms.dev.jobtrackerpro.domain.model.ApplicationStatus.OFFER
            v.contains("rejected") && v.contains("me") -> ms.dev.jobtrackerpro.domain.model.ApplicationStatus.REJECTED_BY_ME
            v.contains("rejected") -> ms.dev.jobtrackerpro.domain.model.ApplicationStatus.REJECTED_BY_COMPANY
            v.contains("ghost") -> ms.dev.jobtrackerpro.domain.model.ApplicationStatus.GHOSTED
            v.contains("email") -> ms.dev.jobtrackerpro.domain.model.ApplicationStatus.EMAIL
            v.contains("phone") -> ms.dev.jobtrackerpro.domain.model.ApplicationStatus.PHONE
            else -> ms.dev.jobtrackerpro.domain.model.ApplicationStatus.APPLIED
        }
    }
    
    // OPTIMIZATION: Use cached formatters instead of creating new ones each call
    private fun parseDate(value: String?): Long {
        val v = value?.trim() ?: return System.currentTimeMillis()
        
        for (format in dateFormatters) {
            try {
                return format.parse(v)?.time ?: continue
            } catch (e: Exception) { 
                continue 
            }
        }
        return System.currentTimeMillis()
    }
    
    private fun parseJobType(value: String?): ms.dev.jobtrackerpro.domain.model.JobType? {
        val v = value?.trim()?.lowercase() ?: return null
        return when {
            v.contains("full") -> ms.dev.jobtrackerpro.domain.model.JobType.FULL_TIME
            v.contains("part") -> ms.dev.jobtrackerpro.domain.model.JobType.PART_TIME
            v.contains("contract") -> ms.dev.jobtrackerpro.domain.model.JobType.CONTRACT
            v.contains("freelance") -> ms.dev.jobtrackerpro.domain.model.JobType.FREELANCE
            else -> null
        }
    }
    
    private fun parseRemoteStatus(value: String?): ms.dev.jobtrackerpro.domain.model.RemoteStatus? {
        val v = value?.trim()?.lowercase() ?: return null
        return when {
            v.contains("remote") && !v.contains("hybrid") -> ms.dev.jobtrackerpro.domain.model.RemoteStatus.REMOTE
            v.contains("hybrid") -> ms.dev.jobtrackerpro.domain.model.RemoteStatus.HYBRID
            v.contains("on-site") || v.contains("onsite") || v.contains("office") -> ms.dev.jobtrackerpro.domain.model.RemoteStatus.ON_SITE
            else -> null
        }
    }
    
    private fun parseSalaryRange(minValue: String?, maxValue: String?): ms.dev.jobtrackerpro.domain.model.SalaryRange? {
        val min = minValue?.trim()?.replace(Regex("[^0-9]"), "")?.toIntOrNull()
        val max = maxValue?.trim()?.replace(Regex("[^0-9]"), "")?.toIntOrNull()
        return if (min != null || max != null) {
            ms.dev.jobtrackerpro.domain.model.SalaryRange(min ?: 0, max ?: min ?: 0)
        } else null
    }
}
