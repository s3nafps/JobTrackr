# Requirements Document

## Introduction

Job Tracker Pro is a personal job application management system for Android that helps users track and monitor their job applications throughout the hiring process. The app provides manual data entry, status progression tracking, CV parsing with ATS scoring, analytics, and cloud backup capabilities. The application follows OneUI 8.5 design guidelines with support for both light and dark (AMOLED) themes.

## Glossary

- **Job_Tracker_System**: The Android application that manages job application data and provides tracking functionality
- **Application**: A job application record containing company, position, and status information
- **Status**: The current stage of a job application (Applied, Email Response, Phone Call, Interview, Offer, Rejected, Ghosted)
- **ATS Score**: Applicant Tracking System compatibility score (0-100) measuring CV optimization
- **CV Parser**: Component that extracts structured data from PDF/DOCX resume files
- **Dashboard**: Home screen displaying overview statistics and recent applications
- **Room Database**: Local SQLite database using Android Room persistence library
- **Cloud Backup**: Remote storage of application data to Google Drive, Google Sheets, or Notion

## Requirements

### Requirement 1: Dashboard Overview

**User Story:** As a job seeker, I want to see an overview of my application statistics on the home screen, so that I can quickly understand my job search progress.

#### Acceptance Criteria

1. WHEN the user opens the application THEN the Job_Tracker_System SHALL display a dashboard with greeting message and current date
2. WHEN the dashboard loads THEN the Job_Tracker_System SHALL display quick statistics cards showing total applications, responses received, interviews scheduled, and offers/rejections counts
3. WHEN the dashboard loads THEN the Job_Tracker_System SHALL display a status distribution visualization using a pie chart or ring progress indicator with color-coded segments
4. WHEN the dashboard loads THEN the Job_Tracker_System SHALL display a horizontal scrollable list of 3-5 most recent applications showing company name, position, status badge, and last update date
5. WHEN the user views the dashboard THEN the Job_Tracker_System SHALL provide quick action buttons for adding new application, uploading CV, viewing analytics, and accessing settings

### Requirement 2: Applications List Management

**User Story:** As a job seeker, I want to view and manage all my job applications in a comprehensive list, so that I can track and organize my job search activities.

#### Acceptance Criteria

1. WHEN the user navigates to the applications list THEN the Job_Tracker_System SHALL display all applications as expandable cards showing company logo/initial, company name, position title, status badge, and date applied
2. WHEN the user searches for applications THEN the Job_Tracker_System SHALL filter results by company name, position, or keyword
3. WHEN the user applies filters THEN the Job_Tracker_System SHALL filter applications by status, date range, or company
4. WHEN the user selects sort options THEN the Job_Tracker_System SHALL sort applications by newest, oldest, status, or company name
5. WHEN the user expands an application card THEN the Job_Tracker_System SHALL display location, salary range, application link, notes, status history timeline, and action buttons for edit, update status, delete, and share
6. WHEN no applications exist THEN the Job_Tracker_System SHALL display an empty state illustration with a call-to-action button to add the first application

### Requirement 3: Application Detail and Edit

**User Story:** As a job seeker, I want to view and edit complete details of a job application, so that I can maintain accurate and comprehensive records.

#### Acceptance Criteria

1. WHEN the user opens application details THEN the Job_Tracker_System SHALL display company information fields including company name, website URL, logo, location, size, and industry
2. WHEN the user opens application details THEN the Job_Tracker_System SHALL display position information fields including job title, description, link, required skills, salary range, job type, and remote status
3. WHEN the user opens application details THEN the Job_Tracker_System SHALL display an interactive application timeline showing dates for each status stage (Applied, Email Response, Phone Call, Interview, Offer/Rejection)
4. WHEN the user opens application details THEN the Job_Tracker_System SHALL display communication fields including recruiter name, email, phone, interview type, date/time, and location/link
5. WHEN the user opens application details THEN the Job_Tracker_System SHALL display outcome fields including final status, offer details, rejection reason, rating score, and custom notes
6. WHEN the user saves changes THEN the Job_Tracker_System SHALL persist all modified fields to the local database and update the timestamp

### Requirement 4: Status Update

**User Story:** As a job seeker, I want to quickly update the status of my applications, so that I can keep my records current with minimal effort.

#### Acceptance Criteria

1. WHEN the user initiates a status update THEN the Job_Tracker_System SHALL display a modal with a visual horizontal timeline showing all status stages (Applied, Email, Phone, Interview, Outcome)
2. WHEN the user selects a status stage THEN the Job_Tracker_System SHALL highlight the selected step in primary color and display a checkmark on completed steps
3. WHEN the user selects a status THEN the Job_Tracker_System SHALL display a date picker for the selected status transition
4. WHEN the user updates status THEN the Job_Tracker_System SHALL provide an optional notes field for additional context
5. WHEN the user saves the status update THEN the Job_Tracker_System SHALL record the status change with timestamp in the status history table

### Requirement 5: CV Parser and ATS Scoring

**User Story:** As a job seeker, I want to upload my CV and receive an ATS compatibility score, so that I can optimize my resume for better job matching.

#### Acceptance Criteria

1. WHEN the user uploads a CV file THEN the Job_Tracker_System SHALL accept PDF, DOC, and DOCX file formats
2. WHEN the CV is uploaded THEN the Job_Tracker_System SHALL display a progress indicator with parsing status message
3. WHEN parsing completes THEN the Job_Tracker_System SHALL extract and display structured data including full name, email, phone, location, professional summary, experience sections, education sections, skills as tag chips, and certifications
4. WHEN parsing completes THEN the Job_Tracker_System SHALL calculate and display an overall ATS score (0-100) as a ring progress indicator with color coding (0-30: Red, 31-60: Orange, 61-80: Yellow, 81-100: Green)
5. WHEN displaying ATS score THEN the Job_Tracker_System SHALL show score breakdown metrics for formatting, keyword density, experience relevance, education, skills match, and ATS compatibility
6. WHEN ATS score is calculated THEN the Job_Tracker_System SHALL generate actionable improvement suggestions ranked by impact
7. WHEN comparing CV to a job description THEN the Job_Tracker_System SHALL display keyword match percentage, highlight matching skills in green, and suggest missing skills in orange
8. WHEN the user prints the parsed CV THEN the Job_Tracker_System SHALL produce output that can be parsed back to an equivalent structured representation

### Requirement 6: Analytics and Insights

**User Story:** As a job seeker, I want to view analytics about my job search, so that I can identify patterns and improve my application strategy.

#### Acceptance Criteria

1. WHEN the user views analytics THEN the Job_Tracker_System SHALL display summary statistics including total applications, response rate percentage, interview rate percentage, average time to response in days, and success rate percentage
2. WHEN the user views analytics THEN the Job_Tracker_System SHALL display a status distribution pie/doughnut chart with tappable segments for filtering
3. WHEN the user views analytics THEN the Job_Tracker_System SHALL display a line graph showing applications over time for the last 6-12 months
4. WHEN the user views analytics THEN the Job_Tracker_System SHALL display a bar chart showing average days between each status stage
5. WHEN the user views analytics THEN the Job_Tracker_System SHALL display a horizontal bar chart showing top 5 companies by response rate
6. WHEN the user applies date filters THEN the Job_Tracker_System SHALL recalculate all analytics for the selected date range
7. WHEN the user exports analytics THEN the Job_Tracker_System SHALL generate reports in CSV or PDF format

### Requirement 7: Settings and Preferences

**User Story:** As a user, I want to customize app settings and manage my data, so that I can personalize my experience and maintain data security.

#### Acceptance Criteria

1. WHEN the user accesses theme settings THEN the Job_Tracker_System SHALL provide options for Light, Dark, and System default themes with True Black AMOLED option for dark mode
2. WHEN the user configures notifications THEN the Job_Tracker_System SHALL allow setting follow-up reminders, interview reminders, and daily summary notifications
3. WHEN the user configures backup THEN the Job_Tracker_System SHALL provide options for Google Drive, Google Sheets, Notion, and CSV export with configurable backup frequency
4. WHEN the user triggers manual backup THEN the Job_Tracker_System SHALL execute backup immediately and display last backup date/time
5. WHEN the user accesses data management THEN the Job_Tracker_System SHALL provide options to clear all data, reset app to default, and clear cache with confirmation dialogs

### Requirement 8: Data Persistence

**User Story:** As a user, I want my application data stored reliably, so that I never lose my job search records.

#### Acceptance Criteria

1. WHEN the user creates or updates an application THEN the Job_Tracker_System SHALL persist data to the local Room database immediately
2. WHEN storing application data THEN the Job_Tracker_System SHALL maintain referential integrity between JobApplications, StatusHistory, and Communications tables
3. WHEN the app launches offline THEN the Job_Tracker_System SHALL load all data from the local database and function without network connectivity
4. WHEN cloud backup is enabled THEN the Job_Tracker_System SHALL sync local data to the configured cloud service on the scheduled frequency
5. WHEN serializing data for backup THEN the Job_Tracker_System SHALL encode records using JSON format that can be deserialized back to equivalent objects

### Requirement 9: User Interface Design

**User Story:** As a user, I want a modern and intuitive interface, so that I can efficiently manage my job applications.

#### Acceptance Criteria

1. WHEN displaying the interface THEN the Job_Tracker_System SHALL follow OneUI 8.5 design guidelines with specified color schemes for light and dark themes
2. WHEN displaying text THEN the Job_Tracker_System SHALL use Samsung One Sans font family with fallback to Roboto, following specified typography scale
3. WHEN displaying interactive elements THEN the Job_Tracker_System SHALL apply card entrance animations, status update transitions, chart animations, and ripple effects on button taps
4. WHEN the app is loading data THEN the Job_Tracker_System SHALL display skeleton loading screens
5. WHEN the user performs destructive actions THEN the Job_Tracker_System SHALL display snackbar with undo option and provide haptic feedback

### Requirement 10: Responsive Design

**User Story:** As a user, I want the app to work well on both phones and tablets, so that I can use it on any Android device.

#### Acceptance Criteria

1. WHEN running on a phone THEN the Job_Tracker_System SHALL display single-column layouts optimized for portrait orientation
2. WHEN running on a tablet THEN the Job_Tracker_System SHALL display multi-column layouts utilizing available screen space
3. WHEN the device orientation changes THEN the Job_Tracker_System SHALL adapt the layout while preserving user state and scroll position
