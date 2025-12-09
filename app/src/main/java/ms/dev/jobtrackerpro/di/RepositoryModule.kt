package ms.dev.jobtrackerpro.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ms.dev.jobtrackerpro.data.repository.ApplicationRepositoryImpl
import ms.dev.jobtrackerpro.data.repository.BackupRepositoryImpl
import ms.dev.jobtrackerpro.data.repository.CommunicationsRepositoryImpl
import ms.dev.jobtrackerpro.data.repository.StatusHistoryRepositoryImpl
import ms.dev.jobtrackerpro.domain.repository.ApplicationRepository
import ms.dev.jobtrackerpro.domain.repository.BackupRepository
import ms.dev.jobtrackerpro.domain.repository.CommunicationsRepository
import ms.dev.jobtrackerpro.domain.repository.StatusHistoryRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindApplicationRepository(
        impl: ApplicationRepositoryImpl
    ): ApplicationRepository
    
    @Binds
    @Singleton
    abstract fun bindStatusHistoryRepository(
        impl: StatusHistoryRepositoryImpl
    ): StatusHistoryRepository
    
    @Binds
    @Singleton
    abstract fun bindCommunicationsRepository(
        impl: CommunicationsRepositoryImpl
    ): CommunicationsRepository
    
    @Binds
    @Singleton
    abstract fun bindBackupRepository(
        impl: BackupRepositoryImpl
    ): BackupRepository
}
