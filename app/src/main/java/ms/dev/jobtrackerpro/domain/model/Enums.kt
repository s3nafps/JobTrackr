package ms.dev.jobtrackerpro.domain.model

/**
 * Application status enum representing the stages of a job application.
 */
enum class ApplicationStatus(val displayName: String) {
    APPLIED("Applied"),
    EMAIL("Email Response"),
    PHONE("Phone Call"),
    INTERVIEW("Interview"),
    OFFER("Offer"),
    REJECTED_BY_COMPANY("Rejected by Company"),
    REJECTED_BY_ME("Rejected by Me"),
    GHOSTED("Ghosted");
    
    companion object {
        fun fromString(value: String): ApplicationStatus {
            return entries.find { it.name == value } ?: APPLIED
        }
    }
}

/**
 * Job type enum.
 */
enum class JobType(val displayName: String) {
    FULL_TIME("Full-time"),
    PART_TIME("Part-time"),
    CONTRACT("Contract"),
    FREELANCE("Freelance");
    
    companion object {
        fun fromString(value: String?): JobType? {
            return value?.let { entries.find { e -> e.name == it } }
        }
    }
}

/**
 * Remote status enum.
 */
enum class RemoteStatus(val displayName: String) {
    REMOTE("Fully Remote"),
    HYBRID("Hybrid"),
    ON_SITE("On-site");
    
    companion object {
        fun fromString(value: String?): RemoteStatus? {
            return value?.let { entries.find { e -> e.name == it } }
        }
    }
}

/**
 * Company size enum.
 */
enum class CompanySize(val displayName: String) {
    STARTUP("Startup"),
    SMB("Small/Medium Business"),
    ENTERPRISE("Enterprise");
    
    companion object {
        fun fromString(value: String?): CompanySize? {
            return value?.let { entries.find { e -> e.name == it } }
        }
    }
}

/**
 * Communication type enum.
 */
enum class CommunicationType(val displayName: String) {
    EMAIL("Email"),
    PHONE_CALL("Phone Call"),
    IN_PERSON_INTERVIEW("In-Person Interview"),
    VIDEO_INTERVIEW("Video Interview");
    
    companion object {
        fun fromString(value: String?): CommunicationType? {
            return value?.let { entries.find { e -> e.name == it } }
        }
    }
}

/**
 * Backup type enum.
 */
enum class BackupType(val displayName: String) {
    GOOGLE_DRIVE("Google Drive"),
    GOOGLE_SHEETS("Google Sheets"),
    NOTION("Notion"),
    CSV("CSV Export");
    
    companion object {
        fun fromString(value: String): BackupType {
            return entries.find { it.name == value } ?: CSV
        }
    }
}

/**
 * Backup status enum.
 */
enum class BackupStatus {
    PENDING,
    COMPLETED,
    FAILED;
    
    companion object {
        fun fromString(value: String): BackupStatus {
            return entries.find { it.name == value } ?: PENDING
        }
    }
}

/**
 * Sort option enum for applications list.
 */
enum class SortOption(val displayName: String) {
    NEWEST("Newest First"),
    OLDEST("Oldest First"),
    COMPANY("Company Name"),
    STATUS("Status")
}

/**
 * Theme mode enum.
 */
enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}
