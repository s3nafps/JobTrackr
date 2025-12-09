package ms.dev.jobtrackerpro.presentation.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ms.dev.jobtrackerpro.domain.model.BackupType
import ms.dev.jobtrackerpro.domain.model.CloudBackup
import ms.dev.jobtrackerpro.domain.repository.ApplicationRepository
import ms.dev.jobtrackerpro.domain.repository.BackupRepository
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val backupRepository: BackupRepository,
    private val applicationRepository: ApplicationRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            backupRepository.getLastSuccessfulBackup().collect { backup ->
                _uiState.value = _uiState.value.copy(lastBackup = backup)
            }
        }
    }
    
    fun exportToUri(uri: Uri, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isBackingUp = true)
            
            val result = backupRepository.exportToUri(uri)
            
            _uiState.value = _uiState.value.copy(
                isBackingUp = false,
                backupMessage = if (result.isSuccess) "Export completed" else "Export failed"
            )
            
            onComplete(result.isSuccess, uri.path ?: "")
        }
    }
    
    fun clearAllData(onComplete: () -> Unit) {
        viewModelScope.launch {
            applicationRepository.deleteAllApplications()
            _uiState.value = _uiState.value.copy(dataCleared = true)
            onComplete()
        }
    }
    
    fun importFromUri(uri: android.net.Uri, onComplete: (Boolean, Int) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isImporting = true)
            
            val result = backupRepository.importFromUri(uri)
            
            _uiState.value = _uiState.value.copy(
                isImporting = false,
                backupMessage = if (result.isSuccess) {
                    "Imported ${result.getOrDefault(0)} applications"
                } else {
                    "Import failed: ${result.exceptionOrNull()?.message}"
                }
            )
            
            onComplete(result.isSuccess, result.getOrDefault(0))
        }
    }
    
    fun dismissMessage() {
        _uiState.value = _uiState.value.copy(backupMessage = null)
    }
}

data class SettingsUiState(
    val lastBackup: CloudBackup? = null,
    val isBackingUp: Boolean = false,
    val isImporting: Boolean = false,
    val backupMessage: String? = null,
    val dataCleared: Boolean = false
)
