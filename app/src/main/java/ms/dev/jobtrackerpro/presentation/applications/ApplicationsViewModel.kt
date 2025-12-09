package ms.dev.jobtrackerpro.presentation.applications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ms.dev.jobtrackerpro.domain.model.FilterState
import ms.dev.jobtrackerpro.domain.model.JobApplication
import ms.dev.jobtrackerpro.domain.model.SortOption
import ms.dev.jobtrackerpro.domain.repository.ApplicationRepository
import ms.dev.jobtrackerpro.domain.usecase.DeleteApplicationUseCase
import javax.inject.Inject

@HiltViewModel
class ApplicationsViewModel @Inject constructor(
    private val applicationRepository: ApplicationRepository,
    private val deleteApplicationUseCase: DeleteApplicationUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ApplicationsUiState())
    val uiState: StateFlow<ApplicationsUiState> = _uiState.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _sortOption = MutableStateFlow(SortOption.NEWEST)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()
    
    private val _filterState = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filterState.asStateFlow()
    
    init {
        loadApplications()
    }
    
    fun loadApplications() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                applicationRepository.getApplicationsSorted(_sortOption.value).collect { applications ->
                    // OPTIMIZATION: Move filtering to Default dispatcher for CPU-intensive work
                    val filteredApps = withContext(Dispatchers.Default) {
                        applyFilters(applications)
                    }
                    _uiState.value = ApplicationsUiState(
                        isLoading = false,
                        applications = filteredApps,
                        error = null
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
    
    fun search(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            if (query.isBlank()) {
                loadApplications()
            } else {
                applicationRepository.searchApplications(query).collect { applications ->
                    _uiState.value = _uiState.value.copy(applications = applications)
                }
            }
        }
    }
    
    fun setSortOption(option: SortOption) {
        _sortOption.value = option
        loadApplications()
    }
    
    fun setFilter(filter: FilterState) {
        _filterState.value = filter
        loadApplications()
    }
    
    fun clearFilters() {
        _filterState.value = FilterState()
        loadApplications()
    }
    
    fun deleteApplication(applicationId: Long) {
        viewModelScope.launch {
            val result = deleteApplicationUseCase(applicationId)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    showUndoSnackbar = true,
                    deletedApplication = result.getOrNull()
                )
            }
        }
    }
    
    fun undoDelete() {
        viewModelScope.launch {
            deleteApplicationUseCase.undo()
            _uiState.value = _uiState.value.copy(
                showUndoSnackbar = false,
                deletedApplication = null
            )
        }
    }
    
    fun dismissUndoSnackbar() {
        _uiState.value = _uiState.value.copy(
            showUndoSnackbar = false,
            deletedApplication = null
        )
        deleteApplicationUseCase.clearUndoState()
    }
    
    // OPTIMIZATION: Use early returns and pre-computed values for faster filtering
    private fun applyFilters(applications: List<JobApplication>): List<JobApplication> {
        val filter = _filterState.value
        val query = _searchQuery.value.lowercase() // OPTIMIZATION: Lowercase once, not per item
        val hasQuery = query.isNotBlank()
        val hasStatusFilter = filter.status != null
        val hasDateFilter = filter.startDate != null && filter.endDate != null
        
        // OPTIMIZATION: Early return if no filters applied
        if (!hasQuery && !hasStatusFilter && !hasDateFilter) {
            return applications
        }
        
        return applications.filter { app ->
            // OPTIMIZATION: Check cheapest conditions first (status is just enum comparison)
            if (hasStatusFilter && app.status != filter.status) return@filter false
            
            if (hasDateFilter && app.applicationDate !in filter.startDate!!..filter.endDate!!) {
                return@filter false
            }
            
            if (hasQuery) {
                val matchesCompany = app.companyName.lowercase().contains(query)
                val matchesTitle = app.jobTitle.lowercase().contains(query)
                val matchesNotes = app.notes?.lowercase()?.contains(query) == true
                if (!matchesCompany && !matchesTitle && !matchesNotes) return@filter false
            }
            
            true
        }
    }
}

data class ApplicationsUiState(
    val isLoading: Boolean = true,
    val applications: List<JobApplication> = emptyList(),
    val error: String? = null,
    val showUndoSnackbar: Boolean = false,
    val deletedApplication: JobApplication? = null
)
