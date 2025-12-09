package ms.dev.jobtrackerpro.domain.usecase

import kotlinx.coroutines.flow.Flow
import ms.dev.jobtrackerpro.domain.model.DashboardStatistics
import ms.dev.jobtrackerpro.domain.repository.ApplicationRepository
import javax.inject.Inject

/**
 * Use case for getting dashboard statistics.
 * Calculates total applications, responses, interviews, offers, and rejections.
 */
class GetDashboardStatisticsUseCase @Inject constructor(
    private val applicationRepository: ApplicationRepository
) {
    operator fun invoke(): Flow<DashboardStatistics> {
        return applicationRepository.getStatistics()
    }
}
