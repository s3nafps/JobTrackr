package ms.dev.jobtrackerpro.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ms.dev.jobtrackerpro.domain.model.ApplicationStatus
import ms.dev.jobtrackerpro.domain.model.FilterState
import ms.dev.jobtrackerpro.domain.model.JobType
import ms.dev.jobtrackerpro.domain.model.RemoteStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDialog(
    currentFilter: FilterState,
    onApplyFilter: (FilterState) -> Unit,
    onClearFilters: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedStatus by remember { mutableStateOf(currentFilter.status) }
    var selectedJobType by remember { mutableStateOf(currentFilter.jobType) }
    var selectedRemoteStatus by remember { mutableStateOf(currentFilter.remoteStatus) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Filter Applications",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Status Filter
                FilterSection(title = "Status") {
                    StatusFilterChips(
                        selectedStatus = selectedStatus,
                        onStatusSelected = { selectedStatus = it }
                    )
                }
                
                HorizontalDivider()
                
                // Job Type Filter
                FilterSection(title = "Job Type") {
                    JobTypeFilterChips(
                        selectedJobType = selectedJobType,
                        onJobTypeSelected = { selectedJobType = it }
                    )
                }
                
                HorizontalDivider()
                
                // Remote Status Filter
                FilterSection(title = "Work Location") {
                    RemoteStatusFilterChips(
                        selectedRemoteStatus = selectedRemoteStatus,
                        onRemoteStatusSelected = { selectedRemoteStatus = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onApplyFilter(
                        FilterState(
                            status = selectedStatus,
                            jobType = selectedJobType,
                            remoteStatus = selectedRemoteStatus
                        )
                    )
                    onDismiss()
                }
            ) {
                Text("Apply")
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = {
                    selectedStatus = null
                    selectedJobType = null
                    selectedRemoteStatus = null
                    onClearFilters()
                    onDismiss()
                }) {
                    Text("Clear All")
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
private fun FilterSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        content()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StatusFilterChips(
    selectedStatus: ApplicationStatus?,
    onStatusSelected: (ApplicationStatus?) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ApplicationStatus.entries.forEach { status ->
            FilterChip(
                selected = selectedStatus == status,
                onClick = {
                    onStatusSelected(if (selectedStatus == status) null else status)
                },
                label = { Text(status.displayName) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun JobTypeFilterChips(
    selectedJobType: JobType?,
    onJobTypeSelected: (JobType?) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        JobType.entries.forEach { jobType ->
            FilterChip(
                selected = selectedJobType == jobType,
                onClick = {
                    onJobTypeSelected(if (selectedJobType == jobType) null else jobType)
                },
                label = { Text(jobType.displayName) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RemoteStatusFilterChips(
    selectedRemoteStatus: RemoteStatus?,
    onRemoteStatusSelected: (RemoteStatus?) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RemoteStatus.entries.forEach { remoteStatus ->
            FilterChip(
                selected = selectedRemoteStatus == remoteStatus,
                onClick = {
                    onRemoteStatusSelected(if (selectedRemoteStatus == remoteStatus) null else remoteStatus)
                },
                label = { Text(remoteStatus.displayName) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer
                )
            )
        }
    }
}
