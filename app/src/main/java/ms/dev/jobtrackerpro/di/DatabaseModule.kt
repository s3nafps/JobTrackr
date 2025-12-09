package ms.dev.jobtrackerpro.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ms.dev.jobtrackerpro.data.local.AppDatabase
import ms.dev.jobtrackerpro.data.local.dao.CloudBackupDao
import ms.dev.jobtrackerpro.data.local.dao.CommunicationDao
import ms.dev.jobtrackerpro.data.local.dao.JobApplicationDao
import ms.dev.jobtrackerpro.data.local.dao.ParsedResumeDao
import ms.dev.jobtrackerpro.data.local.dao.StatusHistoryDao
import ms.dev.jobtrackerpro.utils.Constants
import javax.inject.Singleton

/**
 * Database module optimized for startup performance.
 * 
 * LAZY INITIALIZATION:
 * - Database is created lazily by Hilt (only when first injected)
 * - DAOs are provided lazily (only when repository needs them)
 * - No eager initialization at app startup
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            Constants.DATABASE_NAME
        )
            // OPTIMIZATION: Allow main thread queries only for quick reads during startup
            // Remove this in production if you want strict background-only access
            // .allowMainThreadQueries() // Uncomment only if needed for specific startup queries
            
            // OPTIMIZATION: Set journal mode to TRUNCATE for better write performance
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .build()
    }
    
    @Provides
    @Singleton
    fun provideJobApplicationDao(database: AppDatabase): JobApplicationDao {
        return database.jobApplicationDao()
    }
    
    @Provides
    @Singleton
    fun provideStatusHistoryDao(database: AppDatabase): StatusHistoryDao {
        return database.statusHistoryDao()
    }
    
    @Provides
    @Singleton
    fun provideCommunicationDao(database: AppDatabase): CommunicationDao {
        return database.communicationDao()
    }
    
    @Provides
    @Singleton
    fun provideParsedResumeDao(database: AppDatabase): ParsedResumeDao {
        return database.parsedResumeDao()
    }
    
    @Provides
    @Singleton
    fun provideCloudBackupDao(database: AppDatabase): CloudBackupDao {
        return database.cloudBackupDao()
    }
}
