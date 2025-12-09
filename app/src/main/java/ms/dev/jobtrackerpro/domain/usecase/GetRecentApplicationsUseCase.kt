package ms.dev.jobtrackerpro.domain.usecase

import kotlinx.coroutines.flow.Flow
import ms.dev.jobtrackerpro.domain.model.JobApplication
import ms.dev.jobtrackerpro.domain.repository.ApplicationRepository
import ms.dev.jobtrackerpro.utils.Constants
import javax.inject.Inject

/**
 * Use case for getting recent applications.
 * Returns applications sorted by updated timestamp, limited to specified count.
 */
class GetRecentApplicationsUseCase @Inject constructor(
    private val applicationRepository: ApplicationRepository
) {
    operator fun invoke(limit: Int = Constants.RECENT_APPLICATIONS_LIMIT): Flow<List<JobApplication>> {
        return applicationRepository.getRecentApplications(limit)
    }
}
