package ms.dev.jobtrackerpro.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import ms.dev.jobtrackerpro.domain.model.ApplicationStatus
import ms.dev.jobtrackerpro.domain.model.DashboardStatistics
import ms.dev.jobtrackerpro.domain.model.JobApplication
import ms.dev.jobtrackerpro.domain.usecase.GetDashboardStatisticsUseCase
import ms.dev.jobtrackerpro.domain.usecase.GetRecentApplicationsUseCase
import javax.inject.Inject

/**
 * ViewModel for the Dashboard screen.
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getDashboardStatisticsUseCase: GetDashboardStatisticsUseCase,
    private val getRecentApplicationsUseCase: GetRecentApplicationsUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
    
    init {
        loadDashboard()
    }
    
    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                combine(
                    getDashboardStatisticsUseCase(),
                    getRecentApplicationsUseCase()
                ) { statistics, recentApplications ->
                    DashboardUiState(
                        isLoading = false,
                        statistics = statistics,
                        recentApplications = recentApplications,
                        error = null
                    )
                }.collect { state ->
                    _uiState.value = state
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "An error occurred"
                )
            }
        }
    }
    
    fun refresh() {
        loadDashboard()
    }
}

/**
 * UI state for the Dashboard screen.
 */
data class DashboardUiState(
    val isLoading: Boolean = true,
    val statistics: DashboardStatistics = DashboardStatistics(),
    val recentApplications: List<JobApplication> = emptyList(),
    val error: String? = null
)
