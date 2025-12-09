package ms.dev.jobtrackerpro.utils

/**
 * Application constants.
 */
object Constants {
    const val DATABASE_NAME = "job_tracker_database"
    const val DATABASE_VERSION = 1
    
    // Backup
    const val BACKUP_FOLDER_NAME = "JobTrackerPro"
    const val BACKUP_FILE_PREFIX = "backup_"
    
    // Preferences
    const val PREFS_NAME = "job_tracker_prefs"
    const val PREF_THEME_MODE = "theme_mode"
    const val PREF_AMOLED_MODE = "amoled_mode"
    const val PREF_BACKUP_FREQUENCY = "backup_frequency"
    const val PREF_LAST_BACKUP_TIME = "last_backup_time"
    
    // Undo
    const val UNDO_TIMEOUT_MS = 5000L
    
    // Recent applications limit
    const val RECENT_APPLICATIONS_LIMIT = 5
}
