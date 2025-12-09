package ms.dev.jobtrackerpro.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ms.dev.jobtrackerpro.domain.model.ApplicationStatus
import ms.dev.jobtrackerpro.domain.model.Communication
import ms.dev.jobtrackerpro.domain.model.JobApplication
import ms.dev.jobtrackerpro.domain.model.JobType
import ms.dev.jobtrackerpro.domain.model.RemoteStatus
import ms.dev.jobtrackerpro.domain.model.SalaryRange
import ms.dev.jobtrackerpro.domain.model.StatusHistory
import ms.dev.jobtrackerpro.domain.repository.ApplicationRepository
import ms.dev.jobtrackerpro.domain.repository.CommunicationsRepository
import ms.dev.jobtrackerpro.domain.repository.StatusHistoryRepository
import ms.dev.jobtrackerpro.domain.usecase.DeleteApplicationUseCase
import ms.dev.jobtrackerpro.domain.usecase.SaveApplicationUseCase
import ms.dev.jobtrackerpro.domain.usecase.UpdateApplicationStatusUseCase
import javax.inject.Inject

@HiltViewModel
class ApplicationDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val applicationRepository: ApplicationRepository,
    private val statusHistoryRepository: StatusHistoryRepository,
    private val communicationsRepository: CommunicationsRepository,
    private val saveApplicationUseCase: SaveApplicationUseCase,
    private val updateApplicationStatusUseCase: UpdateApplicationStatusUseCase,
    private val deleteApplicationUseCase: DeleteApplicationUseCase
) : ViewModel() {
    
    private val applicationId: Long = savedStateHandle.get<Long>("applicationId") ?: 0L
    
    private val _uiState = MutableStateFlow(ApplicationDetailUiState())
    val uiState: StateFlow<ApplicationDetailUiState> = _uiState.asStateFlow()
    
    init {
        if (applicationId > 0) {
            loadApplication()
        } else {
            // New application mode
            _uiState.value = ApplicationDetailUiState(
                isLoading = false,
                isNewApplication = true,
                application = createEmptyApplication()
            )
        }
    }
    
    private fun loadApplication() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                applicationRepository.getApplicationById(applicationId).collect { application ->
                    if (application != null) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            application = application,
                            isNewApplication = false
                        )
                        loadStatusHistory()
                        loadCommunications()
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    private fun loadStatusHistory() {
        viewModelScope.launch {
            statusHistoryRepository.getStatusHistory(applicationId).collect { history ->
                _uiState.value = _uiState.value.copy(statusHistory = history)
            }
        }
    }
    
    private fun loadCommunications() {
        viewModelScope.launch {
            communicationsRepository.getCommunications(applicationId).collect { communications ->
                _uiState.value = _uiState.value.copy(communications = communications)
            }
        }
    }
    
    fun updateCompanyName(name: String) {
        _uiState.value = _uiState.value.copy(
            application = _uiState.value.application.copy(companyName = name)
        )
    }
    
    fun updateJobTitle(title: String) {
        _uiState.value = _uiState.value.copy(
            application = _uiState.value.application.copy(jobTitle = title)
        )
    }
    
    fun updateCompanyLocation(location: String) {
        _uiState.value = _uiState.value.copy(
            application = _uiState.value.application.copy(companyLocation = location.ifBlank { null })
        )
    }
    
    fun updateJobDescription(description: String) {
        _uiState.value = _uiState.value.copy(
            application = _uiState.value.application.copy(jobDescription = description.ifBlank { null })
        )
    }
    
    fun updateJobLink(link: String) {
        _uiState.value = _uiState.value.copy(
            application = _uiState.value.application.copy(jobLink = link.ifBlank { null })
        )
    }
    
    fun updateSalaryRange(min: Int?, max: Int?) {
        _uiState.value = _uiState.value.copy(
            application = _uiState.value.application.copy(
                salaryRange = if (min != null || max != null) SalaryRange(min, max) else null
            )
        )
    }
    
    fun updateJobType(jobType: JobType?) {
        _uiState.value = _uiState.value.copy(
            application = _uiState.value.application.copy(jobType = jobType)
        )
    }
    
    fun updateRemoteStatus(remoteStatus: RemoteStatus?) {
        _uiState.value = _uiState.value.copy(
            application = _uiState.value.application.copy(remoteStatus = remoteStatus)
        )
    }
    
    fun updateNotes(notes: String) {
        _uiState.value = _uiState.value.copy(
            application = _uiState.value.application.copy(notes = notes.ifBlank { null })
        )
    }
    
    fun updateRating(rating: Int) {
        _uiState.value = _uiState.value.copy(
            application = _uiState.value.application.copy(rating = rating)
        )
    }
    
    fun updateApplicationDate(date: Long) {
        _uiState.value = _uiState.value.copy(
            application = _uiState.value.application.copy(applicationDate = date)
        )
    }
    
    fun saveApplication(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            
            val result = saveApplicationUseCase(_uiState.value.application)
            
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(isSaving = false)
                onSuccess()
            } else {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = result.exceptionOrNull()?.message
                )
            }
        }
    }
    
    fun updateStatus(status: ApplicationStatus, date: Long, notes: String?) {
        viewModelScope.launch {
            val result = updateApplicationStatusUseCase(applicationId, status, date, notes)
            if (result.isFailure) {
                _uiState.value = _uiState.value.copy(
                    error = result.exceptionOrNull()?.message
                )
            }
        }
    }
    
    fun deleteApplication(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = deleteApplicationUseCase(applicationId)
            if (result.isSuccess) {
                onSuccess()
            } else {
                _uiState.value = _uiState.value.copy(
                    error = result.exceptionOrNull()?.message
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    private fun createEmptyApplication(): JobApplication {
        return JobApplication(
            companyName = "",
            jobTitle = "",
            applicationDate = System.currentTimeMillis(),
            status = ApplicationStatus.APPLIED
        )
    }
}

data class ApplicationDetailUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isNewApplication: Boolean = false,
    val application: JobApplication = JobApplication(
        companyName = "",
        jobTitle = "",
        applicationDate = System.currentTimeMillis(),
        status = ApplicationStatus.APPLIED
    ),
    val statusHistory: List<StatusHistory> = emptyList(),
    val communications: List<Communication> = emptyList(),
    val error: String? = null
)
