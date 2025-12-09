package ms.dev.jobtrackerpro.presentation.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ms.dev.jobtrackerpro.domain.model.Analytics
import ms.dev.jobtrackerpro.domain.model.DateRange
import ms.dev.jobtrackerpro.domain.usecase.GetAnalyticsUseCase
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val getAnalyticsUseCase: GetAnalyticsUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()
    
    private val _selectedDateRange = MutableStateFlow(DateRangeOption.ALL_TIME)
    val selectedDateRange: StateFlow<DateRangeOption> = _selectedDateRange.asStateFlow()
    
    init {
        loadAnalytics()
    }
    
    fun loadAnalytics() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val dateRange = when (_selectedDateRange.value) {
                    DateRangeOption.LAST_MONTH -> DateRange.lastMonth()
                    DateRangeOption.LAST_QUARTER -> DateRange.lastQuarter()
                    DateRangeOption.LAST_YEAR -> DateRange.lastYear()
                    DateRangeOption.ALL_TIME -> null
                }
                
                getAnalyticsUseCase(dateRange).collect { analytics ->
                    _uiState.value = AnalyticsUiState(
                        isLoading = false,
                        analytics = analytics
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    fun setDateRange(option: DateRangeOption) {
        _selectedDateRange.value = option
        loadAnalytics()
    }
}

data class AnalyticsUiState(
    val isLoading: Boolean = true,
    val analytics: Analytics = Analytics(),
    val error: String? = null
)

enum class DateRangeOption(val displayName: String) {
    LAST_MONTH("Last Month"),
    LAST_QUARTER("Last Quarter"),
    LAST_YEAR("Last Year"),
    ALL_TIME("All Time")
}
