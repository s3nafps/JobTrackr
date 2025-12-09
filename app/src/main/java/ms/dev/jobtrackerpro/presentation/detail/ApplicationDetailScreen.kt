package ms.dev.jobtrackerpro.presentation.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ms.dev.jobtrackerpro.domain.model.ApplicationStatus
import ms.dev.jobtrackerpro.domain.model.JobType
import ms.dev.jobtrackerpro.domain.model.RemoteStatus
import ms.dev.jobtrackerpro.presentation.components.StatusBadge
import ms.dev.jobtrackerpro.utils.DateUtils
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: ApplicationDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showStatusDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = uiState.application.applicationDate
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (uiState.isNewApplication) "Add Application" else "Edit Application")
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (!uiState.isNewApplication) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.saveApplication(onNavigateBack) },
                icon = { Icon(Icons.Default.Save, "Save") },
                text = { Text(if (uiState.isNewApplication) "Add Application" else "Save Changes") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Status Badge
                if (!uiState.isNewApplication) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Status", style = MaterialTheme.typography.titleMedium)
                        StatusBadge(status = uiState.application.status)
                    }
                    
                    OutlinedButton(
                        onClick = { showStatusDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Update Status")
                    }
                }
                
                // Company Information Section
                SectionHeader("Company Information")
                
                OutlinedTextField(
                    value = uiState.application.companyName,
                    onValueChange = { viewModel.updateCompanyName(it) },
                    label = { Text("Company Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = uiState.application.companyName.isBlank()
                )
                
                OutlinedTextField(
                    value = uiState.application.companyLocation ?: "",
                    onValueChange = { viewModel.updateCompanyLocation(it) },
                    label = { Text("Location") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                // Position Information Section
                SectionHeader("Position Information")
                
                OutlinedTextField(
                    value = uiState.application.jobTitle,
                    onValueChange = { viewModel.updateJobTitle(it) },
                    label = { Text("Job Title *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = uiState.application.jobTitle.isBlank()
                )
                
                OutlinedTextField(
                    value = uiState.application.jobDescription ?: "",
                    onValueChange = { viewModel.updateJobDescription(it) },
                    label = { Text("Job Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
                
                OutlinedTextField(
                    value = uiState.application.jobLink ?: "",
                    onValueChange = { viewModel.updateJobLink(it) },
                    label = { Text("Job Link") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                // Salary Range
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.application.salaryRange?.min?.toString() ?: "",
                        onValueChange = { 
                            viewModel.updateSalaryRange(
                                it.toIntOrNull(),
                                uiState.application.salaryRange?.max
                            )
                        },
                        label = { Text("Min Salary") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = uiState.application.salaryRange?.max?.toString() ?: "",
                        onValueChange = { 
                            viewModel.updateSalaryRange(
                                uiState.application.salaryRange?.min,
                                it.toIntOrNull()
                            )
                        },
                        label = { Text("Max Salary") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
                
                // Job Type Dropdown
                var jobTypeExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = jobTypeExpanded,
                    onExpandedChange = { jobTypeExpanded = it }
                ) {
                    OutlinedTextField(
                        value = uiState.application.jobType?.displayName ?: "Select Job Type",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Job Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = jobTypeExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = jobTypeExpanded,
                        onDismissRequest = { jobTypeExpanded = false }
                    ) {
                        JobType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.displayName) },
                                onClick = {
                                    viewModel.updateJobType(type)
                                    jobTypeExpanded = false
                                }
                            )
                        }
                    }
                }
                
                // Remote Status Dropdown
                var remoteExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = remoteExpanded,
                    onExpandedChange = { remoteExpanded = it }
                ) {
                    OutlinedTextField(
                        value = uiState.application.remoteStatus?.displayName ?: "Select Remote Status",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Remote Status") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = remoteExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = remoteExpanded,
                        onDismissRequest = { remoteExpanded = false }
                    ) {
                        RemoteStatus.entries.forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status.displayName) },
                                onClick = {
                                    viewModel.updateRemoteStatus(status)
                                    remoteExpanded = false
                                }
                            )
                        }
                    }
                }
                
                // Application Date Section
                SectionHeader("Application Date")
                
                OutlinedTextField(
                    value = DateUtils.formatDate(uiState.application.applicationDate),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("When did you apply?") },
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.CalendarMonth, "Select Date")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }
                )
                
                // Notes Section
                SectionHeader("Notes")
                
                OutlinedTextField(
                    value = uiState.application.notes ?: "",
                    onValueChange = { viewModel.updateNotes(it) },
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
                
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
    
    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { viewModel.updateApplicationDate(it) }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    
    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Application") },
            text = { Text("Are you sure you want to delete this application?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteApplication(onNavigateBack)
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Status Update Dialog
    if (showStatusDialog) {
        StatusUpdateDialog(
            currentStatus = uiState.application.status,
            onDismiss = { showStatusDialog = false },
            onStatusSelected = { status, date, notes ->
                viewModel.updateStatus(status, date, notes)
                showStatusDialog = false
            }
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
private fun StatusUpdateDialog(
    currentStatus: ApplicationStatus,
    onDismiss: () -> Unit,
    onStatusSelected: (ApplicationStatus, Long, String?) -> Unit
) {
    var selectedStatus by remember { mutableStateOf(currentStatus) }
    var notes by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Status") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ApplicationStatus.entries.forEach { status ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedStatus == status,
                            onClick = { selectedStatus = status }
                        )
                        Text(status.displayName)
                    }
                }
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onStatusSelected(selectedStatus, System.currentTimeMillis(), notes.ifBlank { null })
                }
            ) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
