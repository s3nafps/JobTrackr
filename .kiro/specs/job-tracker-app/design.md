# Design Document: Job Tracker Pro

## Overview

Job Tracker Pro is an Android application built using MVVM architecture with Kotlin, designed to help job seekers manage and monitor their job applications. The app provides comprehensive tracking from application submission through final outcome, with CV parsing capabilities, ATS scoring, analytics, and cloud backup functionality.

The application follows OneUI 8.5 design guidelines with Material Design 3 components, supporting both light and dark (AMOLED) themes. It uses Room database for local persistence with an offline-first approach, and integrates with Google Drive for cloud backup.

## Architecture

The application follows the MVVM (Model-View-ViewModel) architectural pattern with Clean Architecture principles:

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                        │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │  Activities │  │  Fragments  │  │  Jetpack Compose    │  │
│  │  /Screens   │  │             │  │  UI Components      │  │
│  └──────┬──────┘  └──────┬──────┘  └──────────┬──────────┘  │
│         │                │                     │             │
│         └────────────────┼─────────────────────┘             │
│                          ▼                                   │
│              ┌───────────────────────┐                       │
│              │     ViewModels        │                       │
│              │  (StateFlow/LiveData) │                       │
│              └───────────┬───────────┘                       │
└──────────────────────────┼───────────────────────────────────┘
                           │
┌──────────────────────────┼───────────────────────────────────┐
│                    Domain Layer                              │
│              ┌───────────▼───────────┐                       │
│              │      Use Cases        │                       │
│              │  (Business Logic)     │                       │
│              └───────────┬───────────┘                       │
└──────────────────────────┼───────────────────────────────────┘
                           │
┌──────────────────────────┼───────────────────────────────────┐
│                     Data Layer                               │
│              ┌───────────▼───────────┐                       │
│              │     Repositories      │                       │
│              └───────────┬───────────┘                       │
│         ┌────────────────┼────────────────┐                  │
│         ▼                ▼                ▼                  │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐          │
│  │ Room DAOs   │  │ CV Parser   │  │ Cloud Sync  │          │
│  │ (Local DB)  │  │ (PDF/DOCX)  │  │ (Drive API) │          │
│  └─────────────┘  └─────────────┘  └─────────────┘          │
└──────────────────────────────────────────────────────────────┘
```

### Dependency Injection

Hilt is used for dependency injection with the following module structure:

- **DatabaseModule**: Provides Room database and DAOs
- **RepositoryModule**: Provides repository implementations
- **NetworkModule**: Provides Retrofit and OkHttp clients for cloud sync
- **ParserModule**: Provides CV parsing utilities

## Components and Interfaces

### UI Layer Components

#### Screens (Jetpack Compose)

1. **DashboardScreen**: Home screen with statistics overview
2. **ApplicationsListScreen**: Comprehensive list with search/filter
3. **ApplicationDetailScreen**: Full application details and editing
4. **StatusUpdateDialog**: Modal for quick status updates
5. **CVParserScreen**: CV upload and ATS scoring
6. **AnalyticsScreen**: Charts and insights
7. **SettingsScreen**: App configuration

#### ViewModels

```kotlin
interface DashboardViewModel {
    val dashboardState: StateFlow<DashboardUiState>
    fun loadDashboard()
    fun refreshStatistics()
}

interface ApplicationsViewModel {
    val applicationsState: StateFlow<ApplicationsUiState>
    val searchQuery: StateFlow<String>
    val filterState: StateFlow<FilterState>
    fun loadApplications()
    fun search(query: String)
    fun applyFilter(filter: FilterState)
    fun sortBy(sortOption: SortOption)
    fun deleteApplication(id: Long)
}

interface ApplicationDetailViewModel {
    val applicationState: StateFlow<ApplicationDetailUiState>
    fun loadApplication(id: Long)
    fun saveApplication(application: JobApplication)
    fun updateStatus(status: ApplicationStatus, date: Long, notes: String?)
    fun deleteApplication()
}

interface CVParserViewModel {
    val parserState: StateFlow<CVParserUiState>
    fun parseCV(uri: Uri)
    fun calculateATSScore(parsedResume: ParsedResume)
    fun compareWithJob(jobDescription: String)
    fun saveParseResult(parsedResume: ParsedResume)
}

interface AnalyticsViewModel {
    val analyticsState: StateFlow<AnalyticsUiState>
    fun loadAnalytics(dateRange: DateRange)
    fun exportReport(format: ExportFormat)
}

interface SettingsViewModel {
    val settingsState: StateFlow<SettingsUiState>
    fun updateTheme(theme: ThemeMode)
    fun updateNotificationSettings(settings: NotificationSettings)
    fun triggerBackup()
    fun clearAllData()
}
```

### Domain Layer Components

#### Use Cases

```kotlin
// Application Management
class GetDashboardStatisticsUseCase(repository: ApplicationRepository)
class GetApplicationsUseCase(repository: ApplicationRepository)
class GetApplicationByIdUseCase(repository: ApplicationRepository)
class SaveApplicationUseCase(repository: ApplicationRepository)
class UpdateApplicationStatusUseCase(repository: ApplicationRepository, statusRepository: StatusHistoryRepository)
class DeleteApplicationUseCase(repository: ApplicationRepository)
class SearchApplicationsUseCase(repository: ApplicationRepository)

// CV Parsing
class ParseCVUseCase(parser: CVParser)
class CalculateATSScoreUseCase(scorer: ATSScorer)
class CompareWithJobUseCase(matcher: JobMatcher)

// Analytics
class GetAnalyticsUseCase(repository: ApplicationRepository)
class ExportReportUseCase(repository: ApplicationRepository, exporter: ReportExporter)

// Backup
class BackupDataUseCase(repository: BackupRepository)
class RestoreDataUseCase(repository: BackupRepository)
```

### Data Layer Components

#### Repositories

```kotlin
interface ApplicationRepository {
    fun getAllApplications(): Flow<List<JobApplication>>
    fun getApplicationById(id: Long): Flow<JobApplication?>
    fun getRecentApplications(limit: Int): Flow<List<JobApplication>>
    fun searchApplications(query: String): Flow<List<JobApplication>>
    fun filterApplications(filter: FilterState): Flow<List<JobApplication>>
    suspend fun insertApplication(application: JobApplication): Long
    suspend fun updateApplication(application: JobApplication)
    suspend fun deleteApplication(id: Long)
    fun getStatistics(): Flow<DashboardStatistics>
}

interface StatusHistoryRepository {
    fun getStatusHistory(applicationId: Long): Flow<List<StatusHistory>>
    suspend fun insertStatusHistory(statusHistory: StatusHistory)
}

interface CommunicationsRepository {
    fun getCommunications(applicationId: Long): Flow<List<Communication>>
    suspend fun insertCommunication(communication: Communication)
    suspend fun updateCommunication(communication: Communication)
    suspend fun deleteCommunication(id: Long)
}

interface ParsedResumeRepository {
    fun getActiveResume(): Flow<ParsedResume?>
    suspend fun saveResume(resume: ParsedResume)
    suspend fun deleteResume(id: Long)
}

interface BackupRepository {
    suspend fun backupToGoogleDrive(): BackupResult
    suspend fun backupToCSV(): BackupResult
    suspend fun restoreFromBackup(backupId: String): RestoreResult
    fun getBackupHistory(): Flow<List<CloudBackup>>
}
```

#### CV Parser Interface

```kotlin
interface CVParser {
    suspend fun parse(inputStream: InputStream, fileType: FileType): ParseResult
}

interface ATSScorer {
    fun calculateScore(parsedResume: ParsedResume): ATSScore
    fun generateSuggestions(score: ATSScore): List<Suggestion>
}

interface JobMatcher {
    fun compareWithJob(resume: ParsedResume, jobDescription: String): MatchResult
}

interface ResumePrinter {
    fun print(parsedResume: ParsedResume): String
}
```

## Data Models

### Entities (Room Database)

```kotlin
@Entity(tableName = "job_applications")
data class JobApplicationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "company_name") val companyName: String,
    @ColumnInfo(name = "job_title") val jobTitle: String,
    @ColumnInfo(name = "application_date") val applicationDate: Long,
    @ColumnInfo(name = "status") val status: String,
    @ColumnInfo(name = "company_location") val companyLocation: String?,
    @ColumnInfo(name = "job_description") val jobDescription: String?,
    @ColumnInfo(name = "job_link") val jobLink: String?,
    @ColumnInfo(name = "salary_min") val salaryMin: Int?,
    @ColumnInfo(name = "salary_max") val salaryMax: Int?,
    @ColumnInfo(name = "job_type") val jobType: String?,
    @ColumnInfo(name = "remote_status") val remoteStatus: String?,
    @ColumnInfo(name = "company_size") val companySize: String?,
    @ColumnInfo(name = "industry") val industry: String?,
    @ColumnInfo(name = "notes") val notes: String?,
    @ColumnInfo(name = "rating") val rating: Int?,
    @ColumnInfo(name = "company_website") val companyWebsite: String?,
    @ColumnInfo(name = "created_timestamp") val createdTimestamp: Long,
    @ColumnInfo(name = "updated_timestamp") val updatedTimestamp: Long
)

@Entity(
    tableName = "status_history",
    foreignKeys = [ForeignKey(
        entity = JobApplicationEntity::class,
        parentColumns = ["id"],
        childColumns = ["application_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class StatusHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "application_id") val applicationId: Long,
    @ColumnInfo(name = "status") val status: String,
    @ColumnInfo(name = "status_date") val statusDate: Long,
    @ColumnInfo(name = "notes") val notes: String?,
    @ColumnInfo(name = "timestamp") val timestamp: Long
)

@Entity(
    tableName = "communications",
    foreignKeys = [ForeignKey(
        entity = JobApplicationEntity::class,
        parentColumns = ["id"],
        childColumns = ["application_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class CommunicationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "application_id") val applicationId: Long,
    @ColumnInfo(name = "recruiter_name") val recruiterName: String?,
    @ColumnInfo(name = "recruiter_email") val recruiterEmail: String?,
    @ColumnInfo(name = "recruiter_phone") val recruiterPhone: String?,
    @ColumnInfo(name = "communication_type") val communicationType: String?,
    @ColumnInfo(name = "communication_date") val communicationDate: Long?,
    @ColumnInfo(name = "communication_notes") val communicationNotes: String?
)

@Entity(tableName = "parsed_resumes")
data class ParsedResumeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "full_name") val fullName: String?,
    @ColumnInfo(name = "email") val email: String?,
    @ColumnInfo(name = "phone") val phone: String?,
    @ColumnInfo(name = "location") val location: String?,
    @ColumnInfo(name = "summary") val summary: String?,
    @ColumnInfo(name = "skills") val skills: String, // JSON array
    @ColumnInfo(name = "experiences") val experiences: String, // JSON array
    @ColumnInfo(name = "education") val education: String, // JSON array
    @ColumnInfo(name = "certifications") val certifications: String, // JSON array
    @ColumnInfo(name = "ats_score") val atsScore: Int?,
    @ColumnInfo(name = "parsed_timestamp") val parsedTimestamp: Long,
    @ColumnInfo(name = "is_active") val isActive: Boolean = true
)

@Entity(tableName = "cloud_backups")
data class CloudBackupEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "backup_type") val backupType: String,
    @ColumnInfo(name = "backup_timestamp") val backupTimestamp: Long,
    @ColumnInfo(name = "backup_status") val backupStatus: String,
    @ColumnInfo(name = "backup_file_id") val backupFileId: String?,
    @ColumnInfo(name = "backup_location") val backupLocation: String?
)
```

### Domain Models

```kotlin
data class JobApplication(
    val id: Long = 0,
    val companyName: String,
    val jobTitle: String,
    val applicationDate: Long,
    val status: ApplicationStatus,
    val companyLocation: String? = null,
    val jobDescription: String? = null,
    val jobLink: String? = null,
    val salaryRange: SalaryRange? = null,
    val jobType: JobType? = null,
    val remoteStatus: RemoteStatus? = null,
    val companySize: CompanySize? = null,
    val industry: String? = null,
    val notes: String? = null,
    val rating: Int? = null,
    val companyWebsite: String? = null,
    val createdTimestamp: Long = System.currentTimeMillis(),
    val updatedTimestamp: Long = System.currentTimeMillis()
)

enum class ApplicationStatus {
    APPLIED, EMAIL, PHONE, INTERVIEW, OFFER, 
    REJECTED_BY_COMPANY, REJECTED_BY_ME, GHOSTED
}

enum class JobType { FULL_TIME, PART_TIME, CONTRACT, FREELANCE }
enum class RemoteStatus { REMOTE, HYBRID, ON_SITE }
enum class CompanySize { STARTUP, SMB, ENTERPRISE }

data class SalaryRange(val min: Int?, val max: Int?)

data class StatusHistory(
    val id: Long = 0,
    val applicationId: Long,
    val status: ApplicationStatus,
    val statusDate: Long,
    val notes: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class ParsedResume(
    val id: Long = 0,
    val fullName: String?,
    val email: String?,
    val phone: String?,
    val location: String?,
    val summary: String?,
    val skills: List<String>,
    val experiences: List<Experience>,
    val education: List<Education>,
    val certifications: List<String>,
    val atsScore: Int? = null,
    val parsedTimestamp: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)

data class Experience(
    val company: String,
    val title: String,
    val startDate: String?,
    val endDate: String?,
    val description: String?
)

data class Education(
    val institution: String,
    val degree: String,
    val field: String?,
    val graduationDate: String?
)

data class ATSScore(
    val overallScore: Int,
    val formattingScore: Int,
    val keywordScore: Int,
    val experienceScore: Int,
    val educationScore: Int,
    val skillsScore: Int,
    val compatibilityScore: Int
)

data class DashboardStatistics(
    val totalApplications: Int,
    val responsesReceived: Int,
    val interviewsScheduled: Int,
    val offersReceived: Int,
    val rejections: Int,
    val statusDistribution: Map<ApplicationStatus, Int>
)
```

### JSON Serialization Models

```kotlin
// For backup/restore operations
data class BackupData(
    val version: Int,
    val timestamp: Long,
    val applications: List<JobApplicationBackup>,
    val statusHistory: List<StatusHistoryBackup>,
    val communications: List<CommunicationBackup>,
    val parsedResumes: List<ParsedResumeBackup>
)

data class JobApplicationBackup(
    val id: Long,
    val companyName: String,
    val jobTitle: String,
    val applicationDate: Long,
    val status: String,
    val companyLocation: String?,
    val jobDescription: String?,
    val jobLink: String?,
    val salaryMin: Int?,
    val salaryMax: Int?,
    val jobType: String?,
    val remoteStatus: String?,
    val companySize: String?,
    val industry: String?,
    val notes: String?,
    val rating: Int?,
    val companyWebsite: String?,
    val createdTimestamp: Long,
    val updatedTimestamp: Long
)
```


## Correctness Properties

*A property is a characteristic or behavior that should hold true across all valid executions of a system-essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.*

Based on the acceptance criteria analysis, the following correctness properties must be validated through property-based testing:

### Property 1: Statistics Accuracy

*For any* set of job applications in the database, the calculated statistics (total count, responses received, interviews scheduled, offers, rejections) SHALL accurately reflect the actual counts of applications in each status category.

**Validates: Requirements 1.2, 6.1**

### Property 2: Recent Applications Ordering

*For any* list of job applications, the "recent applications" query SHALL return applications sorted by updated timestamp in descending order, limited to the specified count.

**Validates: Requirements 1.4**

### Property 3: Applications List Completeness

*For any* set of job applications stored in the database, the applications list screen SHALL display all applications without omission.

**Validates: Requirements 2.1**

### Property 4: Search Results Relevance

*For any* search query and set of applications, the search results SHALL contain only applications where the company name, position title, or notes contain the search query (case-insensitive).

**Validates: Requirements 2.2**

### Property 5: Filter Results Correctness

*For any* filter criteria (status, date range, company) and set of applications, the filtered results SHALL contain only applications that match ALL specified filter criteria.

**Validates: Requirements 2.3**

### Property 6: Sort Order Correctness

*For any* sort option (newest, oldest, status, company name) and set of applications, the sorted list SHALL be correctly ordered according to the specified sort criteria.

**Validates: Requirements 2.4**

### Property 7: Data Persistence Integrity

*For any* job application that is saved, the data retrieved from the database SHALL match the data that was submitted, and the updated timestamp SHALL be greater than or equal to the previous timestamp.

**Validates: Requirements 3.6, 8.1**

### Property 8: Status History Recording

*For any* status update operation, a corresponding status history record SHALL be created with the correct application ID, status, date, and timestamp.

**Validates: Requirements 4.5**

### Property 9: File Format Acceptance

*For any* file upload attempt, the system SHALL accept files with extensions .pdf, .doc, and .docx, and SHALL reject files with other extensions.

**Validates: Requirements 5.1**

### Property 10: CV Parsing Extraction

*For any* valid CV document containing contact information, the parser SHALL extract at least one of: email (matching email regex pattern), phone (matching phone regex pattern), or name.

**Validates: Requirements 5.3**

### Property 11: ATS Score Bounds and Breakdown

*For any* parsed resume, the calculated ATS score SHALL be within the range 0-100, and the sum of component scores (formatting, keyword, experience, education, skills, compatibility) SHALL equal the overall score.

**Validates: Requirements 5.4, 5.5**

### Property 12: ATS Suggestions Generation

*For any* ATS score with component scores below threshold values, the system SHALL generate at least one improvement suggestion targeting the lowest-scoring component.

**Validates: Requirements 5.6**

### Property 13: Job Match Percentage Calculation

*For any* resume skills list and job description keywords, the match percentage SHALL equal (matched keywords / total job keywords) * 100, rounded to the nearest integer.

**Validates: Requirements 5.7**

### Property 14: CV Print-Parse Round Trip

*For any* valid ParsedResume object, printing the resume to string format and then parsing that string SHALL produce a ParsedResume object equivalent to the original.

**Validates: Requirements 5.8**

### Property 15: Status Distribution Accuracy

*For any* set of applications, the status distribution chart data SHALL have segment values that sum to the total application count, with each segment accurately representing its status count.

**Validates: Requirements 6.2**

### Property 16: Applications Over Time Aggregation

*For any* set of applications and time range, the monthly application counts SHALL accurately reflect the number of applications created in each month.

**Validates: Requirements 6.3**

### Property 17: Status Transition Duration Calculation

*For any* application with status history, the average days between status stages SHALL be calculated as the mean of (later_status_date - earlier_status_date) for all applications with both statuses.

**Validates: Requirements 6.4**

### Property 18: Company Response Rate Ranking

*For any* set of applications grouped by company, the response rate for each company SHALL equal (applications_with_response / total_applications) * 100, and companies SHALL be ranked in descending order by this rate.

**Validates: Requirements 6.5**

### Property 19: Date Range Filter Application

*For any* date range filter applied to analytics, all calculated statistics SHALL only include applications with application_date within the specified range (inclusive).

**Validates: Requirements 6.6**

### Property 20: Backup Timestamp Recording

*For any* successful backup operation, a backup record SHALL be created with timestamp equal to or greater than the operation start time and status set to COMPLETED.

**Validates: Requirements 7.4**

### Property 21: Referential Integrity

*For any* status history or communication record, the referenced application_id SHALL correspond to an existing job application in the database.

**Validates: Requirements 8.2**

### Property 22: Backup Serialization Round Trip

*For any* BackupData object containing applications, status history, and communications, serializing to JSON and deserializing SHALL produce a BackupData object equivalent to the original.

**Validates: Requirements 8.5**

### Property 23: Undo Restoration

*For any* destructive action (delete) followed by undo within the timeout period, the deleted data SHALL be restored to its previous state.

**Validates: Requirements 9.5**

### Property 24: Configuration Change State Preservation

*For any* screen state (scroll position, form inputs, selected items) when orientation changes, the state SHALL be preserved after the configuration change completes.

**Validates: Requirements 10.3**

## Error Handling

### Input Validation Errors

| Error Condition | Handling Strategy |
|----------------|-------------------|
| Empty company name | Display inline error, prevent save |
| Empty job title | Display inline error, prevent save |
| Invalid URL format | Display warning, allow save |
| Invalid email format | Display warning, allow save |
| Invalid phone format | Display warning, allow save |
| Salary min > max | Display error, swap values or prevent save |
| Future application date | Display warning, allow save |

### File Processing Errors

| Error Condition | Handling Strategy |
|----------------|-------------------|
| Unsupported file format | Display error toast, suggest supported formats |
| File too large (>10MB) | Display error with size limit |
| Corrupted PDF/DOCX | Display parsing error, suggest re-upload |
| Empty file | Display error, request valid file |
| Permission denied | Request storage permission, guide user |

### Database Errors

| Error Condition | Handling Strategy |
|----------------|-------------------|
| Insert failure | Retry once, then display error with retry option |
| Update failure | Retry once, preserve local changes, display error |
| Delete failure | Display error, data remains unchanged |
| Query timeout | Display loading timeout, offer retry |
| Database corruption | Attempt recovery, offer restore from backup |

### Network/Cloud Errors

| Error Condition | Handling Strategy |
|----------------|-------------------|
| No internet connection | Queue backup, sync when connected |
| Google Drive auth failure | Prompt re-authentication |
| Backup upload failure | Retry with exponential backoff (3 attempts) |
| Quota exceeded | Notify user, suggest cleanup or upgrade |
| Sync conflict | Local data takes precedence, log conflict |

### UI State Errors

| Error Condition | Handling Strategy |
|----------------|-------------------|
| ViewModel state loss | Restore from SavedStateHandle |
| Navigation failure | Fallback to home screen |
| Chart rendering failure | Display placeholder with error message |
| Animation failure | Gracefully degrade to no animation |

## Testing Strategy

### Dual Testing Approach

The application uses both unit testing and property-based testing for comprehensive coverage:

- **Unit tests** verify specific examples, edge cases, and integration points
- **Property-based tests** verify universal properties that should hold across all inputs

### Property-Based Testing Framework

**Framework**: Kotest with Property-Based Testing module

```kotlin
// build.gradle.kts
testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
testImplementation("io.kotest:kotest-assertions-core:5.8.0")
testImplementation("io.kotest:kotest-property:5.8.0")
```

**Configuration**: Each property test runs minimum 100 iterations.

**Annotation Format**: Each property test is tagged with:
```kotlin
// **Feature: job-tracker-app, Property {number}: {property_text}**
```

### Unit Testing Framework

**Framework**: JUnit 5 with MockK for mocking

```kotlin
testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
testImplementation("io.mockk:mockk:1.13.8")
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
```

### Test Categories

#### 1. Data Layer Tests

- **Repository Tests**: Verify CRUD operations, queries, and data transformations
- **DAO Tests**: Verify Room database operations with in-memory database
- **Mapper Tests**: Verify entity-to-domain model conversions

#### 2. Domain Layer Tests

- **Use Case Tests**: Verify business logic execution
- **Statistics Calculation Tests**: Verify analytics computations
- **ATS Scoring Tests**: Verify scoring algorithm accuracy

#### 3. CV Parser Tests

- **PDF Parsing Tests**: Verify text extraction from PDF files
- **DOCX Parsing Tests**: Verify text extraction from Word documents
- **Section Detection Tests**: Verify regex patterns for section identification
- **Round-Trip Tests**: Verify print-parse consistency

#### 4. Serialization Tests

- **JSON Serialization Tests**: Verify backup data serialization
- **Round-Trip Tests**: Verify serialize-deserialize consistency

#### 5. UI Tests

- **ViewModel Tests**: Verify state management and UI logic
- **Compose UI Tests**: Verify screen rendering and interactions
- **Navigation Tests**: Verify screen transitions

### Test Data Generators (for Property-Based Testing)

```kotlin
// Custom generators for domain models
val jobApplicationArb = arbitrary {
    JobApplication(
        id = Arb.long(1..Long.MAX_VALUE).bind(),
        companyName = Arb.string(1..100).bind(),
        jobTitle = Arb.string(1..100).bind(),
        applicationDate = Arb.long(0..System.currentTimeMillis()).bind(),
        status = Arb.enum<ApplicationStatus>().bind(),
        companyLocation = Arb.string(0..200).orNull().bind(),
        salaryRange = salaryRangeArb.orNull().bind(),
        jobType = Arb.enum<JobType>().orNull().bind(),
        remoteStatus = Arb.enum<RemoteStatus>().orNull().bind()
    )
}

val parsedResumeArb = arbitrary {
    ParsedResume(
        fullName = Arb.string(1..50).orNull().bind(),
        email = Arb.email().orNull().bind(),
        phone = Arb.string(10..15).orNull().bind(),
        location = Arb.string(1..100).orNull().bind(),
        summary = Arb.string(0..500).orNull().bind(),
        skills = Arb.list(Arb.string(1..30), 0..20).bind(),
        experiences = Arb.list(experienceArb, 0..10).bind(),
        education = Arb.list(educationArb, 0..5).bind(),
        certifications = Arb.list(Arb.string(1..50), 0..10).bind()
    )
}
```

### Coverage Requirements

- **Unit test coverage**: >70% line coverage for business logic
- **Property test coverage**: All 24 correctness properties implemented
- **Integration test coverage**: Critical user flows (add application, update status, backup)
