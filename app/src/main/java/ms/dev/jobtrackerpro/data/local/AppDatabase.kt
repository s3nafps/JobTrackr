package ms.dev.jobtrackerpro.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ms.dev.jobtrackerpro.data.local.dao.CloudBackupDao
import ms.dev.jobtrackerpro.data.local.dao.CommunicationDao
import ms.dev.jobtrackerpro.data.local.dao.JobApplicationDao
import ms.dev.jobtrackerpro.data.local.dao.ParsedResumeDao
import ms.dev.jobtrackerpro.data.local.dao.StatusHistoryDao
import ms.dev.jobtrackerpro.data.local.entity.CloudBackupEntity
import ms.dev.jobtrackerpro.data.local.entity.CommunicationEntity
import ms.dev.jobtrackerpro.data.local.entity.JobApplicationEntity
import ms.dev.jobtrackerpro.data.local.entity.ParsedResumeEntity
import ms.dev.jobtrackerpro.data.local.entity.StatusHistoryEntity
import ms.dev.jobtrackerpro.utils.Constants

@Database(
    entities = [
        JobApplicationEntity::class,
        StatusHistoryEntity::class,
        CommunicationEntity::class,
        ParsedResumeEntity::class,
        CloudBackupEntity::class
    ],
    version = Constants.DATABASE_VERSION,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun jobApplicationDao(): JobApplicationDao
    abstract fun statusHistoryDao(): StatusHistoryDao
    abstract fun communicationDao(): CommunicationDao
    abstract fun parsedResumeDao(): ParsedResumeDao
    abstract fun cloudBackupDao(): CloudBackupDao
}
