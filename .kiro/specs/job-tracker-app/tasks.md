# Implementation Plan

- [x] 1. Set up project structure and dependencies

  - [x] 1.1 Configure Gradle dependencies for Room, Hilt, Compose, Retrofit, Kotest, and document parsing libraries


    - Add all required dependencies to build.gradle.kts
    - Configure Hilt plugin and kapt processors
    - Set up Kotest for property-based testing
    - _Requirements: 8.1, 8.2_
  - [x] 1.2 Create package structure following Clean Architecture


    - Create packages: data, domain, presentation, di, utils
    - Create sub-packages for each layer (entities, daos, repositories, usecases, viewmodels, screens)
    - _Requirements: 8.1_
  - [x] 1.3 Set up Hilt dependency injection modules


    - Create DatabaseModule, RepositoryModule, UseCaseModule
    - Configure Application class with @HiltAndroidApp
    - _Requirements: 8.1_

- [x] 2. Implement database layer

  - [x] 2.1 Create Room database entities


    - Implement JobApplicationEntity, StatusHistoryEntity, CommunicationEntity, ParsedResumeEntity, CloudBackupEntity
    - Define foreign key relationships and indices
    - _Requirements: 8.1, 8.2_
  - [x] 2.2 Create Room DAOs


    - Implement JobApplicationDao with CRUD operations and queries
    - Implement StatusHistoryDao, CommunicationDao, ParsedResumeDao, CloudBackupDao
    - _Requirements: 8.1, 8.2_
  - [x] 2.3 Create AppDatabase class


    - Configure Room database with all entities
    - Set up type converters for enums and JSON fields
    - _Requirements: 8.1_
  - [x]* 2.4 Write property test for referential integrity


    - **Property 21: Referential Integrity**
    - **Validates: Requirements 8.2**

- [x] 3. Implement domain models and mappers

  - [x] 3.1 Create domain model data classes


    - Implement JobApplication, StatusHistory, Communication, ParsedResume domain models
    - Create enums: ApplicationStatus, JobType, RemoteStatus, CompanySize
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5_
  - [x] 3.2 Create entity-to-domain mappers


    - Implement bidirectional mappers for all entities
    - Handle JSON serialization for skills, experiences, education arrays
    - _Requirements: 8.1_

  - [x]* 3.3 Write property test for data persistence integrity
    - **Property 7: Data Persistence Integrity**
    - **Validates: Requirements 3.6, 8.1**

- [x] 4. Implement repositories

  - [x] 4.1 Implement ApplicationRepository


    - Create ApplicationRepositoryImpl with all CRUD operations
    - Implement search, filter, and sort queries
    - Implement statistics calculation queries
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 8.1_
  - [x] 4.2 Implement StatusHistoryRepository


    - Create StatusHistoryRepositoryImpl
    - Implement status history recording
    - _Requirements: 4.5_
  - [x] 4.3 Implement CommunicationsRepository and ParsedResumeRepository


    - Create repository implementations for communications and parsed resumes
    - _Requirements: 3.4, 5.3_

  - [x]* 4.4 Write property tests for repository operations
    - **Property 3: Applications List Completeness**
    - **Property 4: Search Results Relevance**
    - **Property 5: Filter Results Correctness**
    - **Property 6: Sort Order Correctness**
    - **Validates: Requirements 2.1, 2.2, 2.3, 2.4**

- [x] 5. Checkpoint - Ensure all tests pass



  - Ensure all tests pass, ask the user if questions arise.

- [x] 6. Implement statistics and analytics use cases

  - [x] 6.1 Implement GetDashboardStatisticsUseCase


    - Calculate total applications, responses, interviews, offers, rejections
    - Calculate status distribution for chart data
    - _Requirements: 1.2, 1.3_
  - [x] 6.2 Implement GetRecentApplicationsUseCase


    - Query applications sorted by updated timestamp, limited to N
    - _Requirements: 1.4_
  - [x] 6.3 Implement GetAnalyticsUseCase


    - Calculate response rate, interview rate, success rate percentages
    - Calculate average time to response
    - Aggregate applications over time by month
    - Calculate status transition durations
    - Calculate company response rates

    - _Requirements: 6.1, 6.2, 6.3, 6.4, 6.5_
  - [x]* 6.4 Write property tests for statistics calculations
    - **Property 1: Statistics Accuracy**
    - **Property 2: Recent Applications Ordering**
    - **Property 15: Status Distribution Accuracy**
    - **Property 16: Applications Over Time Aggregation**
    - **Property 17: Status Transition Duration Calculation**
    - **Property 18: Company Response Rate Ranking**
    - **Validates: Requirements 1.2, 1.4, 6.1, 6.2, 6.3, 6.4, 6.5**

- [x] 7. Implement application management use cases

  - [x] 7.1 Implement SaveApplicationUseCase


    - Validate required fields (company name, job title)
    - Set timestamps on create/update
    - _Requirements: 3.6, 8.1_
  - [x] 7.2 Implement UpdateApplicationStatusUseCase


    - Update application status
    - Create status history record with timestamp
    - _Requirements: 4.5_
  - [x] 7.3 Implement DeleteApplicationUseCase with undo support


    - Soft delete with undo capability within timeout

    - Hard delete after timeout
    - _Requirements: 9.5_
  - [x]* 7.4 Write property tests for status and undo operations
    - **Property 8: Status History Recording**
    - **Property 23: Undo Restoration**
    - **Validates: Requirements 4.5, 9.5**

- [x] 8. Implement date range filtering

  - [x] 8.1 Implement date range filter for analytics



    - Filter all analytics queries by date range
    - Support last month, quarter, year, and custom ranges
    - _Requirements: 6.6_
  - [x]* 8.2 Write property test for date range filtering
    - **Property 19: Date Range Filter Application**
    - **Validates: Requirements 6.6**

- [x] 9. Checkpoint - Ensure all tests pass

  - Ensure all tests pass, ask the user if questions arise.

- [x] 10. Implement CV parser

  - [x] 10.1 Implement PDF text extraction


    - Use Apache PDFBox for PDF parsing
    - Extract plain text from PDF documents
    - _Requirements: 5.1, 5.3_

  - [x] 10.2 Implement DOCX text extraction

    - Use Apache POI for DOCX parsing
    - Extract plain text from Word documents

    - _Requirements: 5.1, 5.3_

  - [x] 10.3 Implement section detection and data extraction
    - Create regex patterns for contact info (email, phone)
    - Create patterns for experience sections (company, title, dates)
    - Create patterns for education sections
    - Extract skills from skills section
    - _Requirements: 5.3_
  - [x] 10.4 Implement ResumePrinter for serialization


    - Create string representation of ParsedResume
    - Ensure output can be parsed back to equivalent structure
    - _Requirements: 5.8_
  - [x]* 10.5 Write property tests for CV parsing

    - **Property 9: File Format Acceptance**
    - **Property 10: CV Parsing Extraction**
    - **Property 14: CV Print-Parse Round Trip**
    - **Validates: Requirements 5.1, 5.3, 5.8**

- [x] 11. Implement ATS scoring

  - [x] 11.1 Implement ATSScorer


    - Calculate formatting score (0-20 points)
    - Calculate keyword score (0-30 points)
    - Calculate experience score (0-20 points)
    - Calculate education score (0-15 points)
    - Calculate skills score (0-10 points)
    - Calculate compatibility score (0-5 points)
    - _Requirements: 5.4, 5.5_

  - [x] 11.2 Implement suggestion generator
    - Generate suggestions based on low-scoring components
    - Rank suggestions by potential impact
    - _Requirements: 5.6_

  - [x] 11.3 Implement JobMatcher for CV-job comparison

    - Extract keywords from job description
    - Calculate match percentage
    - Identify matching and missing skills
    - _Requirements: 5.7_

  - [x]* 11.4 Write property tests for ATS scoring
    - **Property 11: ATS Score Bounds and Breakdown**
    - **Property 12: ATS Suggestions Generation**
    - **Property 13: Job Match Percentage Calculation**
    - **Validates: Requirements 5.4, 5.5, 5.6, 5.7**

- [x] 12. Checkpoint - Ensure all tests pass

  - Ensure all tests pass, ask the user if questions arise.

- [x] 13. Implement backup and serialization

  - [x] 13.1 Implement JSON serialization for backup


    - Create BackupData model with all application data
    - Implement Gson serialization/deserialization
    - _Requirements: 8.5_
  - [x] 13.2 Implement BackupRepository

    - Implement CSV export functionality
    - Implement Google Drive backup (optional, requires API setup)
    - Record backup history with timestamps

    - _Requirements: 7.3, 7.4, 8.4_
  - [x]* 13.3 Write property tests for backup serialization
    - **Property 20: Backup Timestamp Recording**
    - **Property 22: Backup Serialization Round Trip**
    - **Validates: Requirements 7.4, 8.5**

- [x] 14. Implement theme and UI foundation

  - [x] 14.1 Create OneUI 8.5 theme configuration


    - Define light theme colors (Primary: #218C94, Background: #F9F9F6, etc.)
    - Define dark AMOLED theme colors (Background: #000000, etc.)
    - Configure typography with Samsung One Sans / Roboto fallback
    - _Requirements: 9.1, 9.2_
  - [x] 14.2 Create reusable UI components


    - Create StatusBadge composable with color coding
    - Create ApplicationCard composable
    - Create StatisticsCard composable
    - Create skeleton loading composables
    - _Requirements: 9.3, 9.4_

- [x] 15. Implement Dashboard screen

  - [x] 15.1 Create DashboardViewModel


    - Expose dashboard state with statistics and recent applications
    - Handle refresh actions
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5_
  - [x] 15.2 Create DashboardScreen composable


    - Implement greeting header with date
    - Implement statistics cards grid
    - Implement status distribution pie chart
    - Implement recent applications horizontal list
    - Implement FAB and quick action buttons
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5_

- [x] 16. Implement Applications List screen

  - [x] 16.1 Create ApplicationsViewModel


    - Expose applications list state
    - Handle search, filter, and sort operations
    - Handle delete with undo
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5, 2.6_
  - [x] 16.2 Create ApplicationsListScreen composable


    - Implement search bar with filter/sort buttons
    - Implement expandable application cards
    - Implement empty state with CTA
    - Implement swipe-to-delete with undo snackbar
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5, 2.6_

- [x] 17. Implement Application Detail screen



  - [x] 17.1 Create ApplicationDetailViewModel


    - Expose application detail state
    - Handle save, update status, and delete operations
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 3.6_
  - [x] 17.2 Create ApplicationDetailScreen composable


    - Implement collapsible sections for company, position, timeline, communication, outcome
    - Implement form fields with validation
    - Implement interactive status timeline
    - Implement action buttons (save, delete, share)
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 3.6_



- [x] 18. Implement Status Update modal
  - [x] 18.1 Create StatusUpdateDialog composable
    - Implement horizontal timeline with selectable steps
    - Implement date picker for status date
    - Implement optional notes field
    - Implement save/cancel buttons

    - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.5_

- [x] 19. Checkpoint - Ensure all tests pass
  - Ensure all tests pass, ask the user if questions arise.

- [x] 20. Implement CV Parser screen

  - [x] 20.1 Create CVParserViewModel


    - Handle file selection and parsing
    - Expose parsing state and progress
    - Handle ATS score calculation
    - Handle job comparison
    - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5, 5.6, 5.7_

  - [x] 20.2 Create CVParserScreen composable
    - Implement file upload area
    - Implement parsing progress indicator
    - Implement extracted data display with collapsible sections
    - Implement ATS score ring with color coding
    - Implement score breakdown metrics
    - Implement improvement suggestions list
    - Implement job comparison view
    - _Requirements: 5.1, 5.2, 5.3, 5.4, 5.5, 5.6, 5.7_

- [x] 21. Implement Analytics screen


  - [x] 21.1 Create AnalyticsViewModel

    - Expose analytics state with all chart data
    - Handle date range filtering
    - Handle report export
    - _Requirements: 6.1, 6.2, 6.3, 6.4, 6.5, 6.6, 6.7_
  - [x] 21.2 Create AnalyticsScreen composable

    - Implement summary statistics cards
    - Implement status distribution pie chart (MPAndroidChart)
    - Implement applications over time line chart
    - Implement status transition bar chart
    - Implement company response rate horizontal bar chart
    - Implement date range filter
    - Implement export buttons
    - _Requirements: 6.1, 6.2, 6.3, 6.4, 6.5, 6.6, 6.7_

- [x] 22. Implement Settings screen

  - [x] 22.1 Create SettingsViewModel


    - Expose settings state
    - Handle theme changes
    - Handle notification settings
    - Handle backup operations
    - Handle data management operations
    - _Requirements: 7.1, 7.2, 7.3, 7.4, 7.5_

  - [x] 22.2 Create SettingsScreen composable
    - Implement theme selection (Light/Dark/System with AMOLED toggle)
    - Implement notification settings section
    - Implement backup configuration section
    - Implement data management section with confirmation dialogs
    - Implement about section
    - _Requirements: 7.1, 7.2, 7.3, 7.4, 7.5_

- [x] 23. Implement navigation and state preservation


  - [x] 23.1 Set up Navigation Compose

    - Configure NavHost with all screen destinations
    - Implement bottom navigation bar
    - Handle deep links for notifications
    - _Requirements: 1.5_
  - [x] 23.2 Implement state preservation across configuration changes

    - Use SavedStateHandle in ViewModels

    - Preserve scroll positions and form inputs
    - _Requirements: 10.3_
  - [x]* 23.3 Write property test for state preservation

    - **Property 24: Configuration Change State Preservation**
    - **Validates: Requirements 10.3**

- [x] 24. Implement responsive layouts

  - [x] 24.1 Create adaptive layouts for phone and tablet

    - Implement single-column layouts for phone
    - Implement multi-column layouts for tablet
    - Use WindowSizeClass for breakpoints
    - _Requirements: 10.1, 10.2_



- [x] 25. Implement animations and polish
  - [x] 25.1 Add UI animations
    - Implement card entrance animations
    - Implement status update transitions
    - Implement chart animations
    - Add ripple effects and haptic feedback

    - _Requirements: 9.3, 9.5_

- [x] 26. Final Checkpoint - Ensure all tests pass

  - Ensure all tests pass, ask the user if questions arise.
