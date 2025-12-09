package ms.dev.jobtrackerpro.domain.usecase

import kotlinx.coroutines.flow.Flow
import ms.dev.jobtrackerpro.domain.model.Analytics
import ms.dev.jobtrackerpro.domain.model.DateRange
import javax.inject.Inject

/**
 * Use case for getting analytics filtered by date range.
 * Supports last month, quarter, year, and custom ranges.
 */
class GetFilteredAnalyticsUseCase @Inject constructor(
    private val getAnalyticsUseCase: GetAnalyticsUseCase
) {
    operator fun invoke(dateRange: DateRange): Flow<Analytics> {
        return getAnalyticsUseCase(dateRange)
    }
    
    fun lastMonth(): Flow<Analytics> {
        return getAnalyticsUseCase(DateRange.lastMonth())
    }
    
    fun lastQuarter(): Flow<Analytics> {
        return getAnalyticsUseCase(DateRange.lastQuarter())
    }
    
    fun lastYear(): Flow<Analytics> {
        return getAnalyticsUseCase(DateRange.lastYear())
    }
    
    fun allTime(): Flow<Analytics> {
        return getAnalyticsUseCase(null)
    }
}
