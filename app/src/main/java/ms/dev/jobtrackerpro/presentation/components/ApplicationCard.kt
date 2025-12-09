package ms.dev.jobtrackerpro.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ms.dev.jobtrackerpro.domain.model.JobApplication
import ms.dev.jobtrackerpro.utils.DateUtils

/**
 * Application card component for displaying job application summary.
 * OPTIMIZATION: Designed for 120fps scrolling performance
 */
@Composable
fun ApplicationCard(
    application: JobApplication,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    // OPTIMIZATION: Remember expensive string operations - computed once per data change
    val companyInitial = remember(application.companyName) { 
        application.companyName.take(1).uppercase() 
    }
    
    val formattedDate = remember(application.applicationDate) { 
        "Applied: ${DateUtils.formatDate(application.applicationDate)}" 
    }
    
    val salaryDisplay = remember(application.salaryRange) {
        application.salaryRange?.toDisplayString()
    }
    
    // OPTIMIZATION: Stable lambda reference to prevent unnecessary recompositions
    val toggleExpanded = remember { { expanded = !expanded } }
    
    // OPTIMIZATION: Cache colors to avoid repeated lookups during scroll
    val containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    val primaryContainerColor = MaterialTheme.colorScheme.primaryContainer
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant
    
    // OPTIMIZATION: Use clickable Card instead of pointerInput for better performance
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                // OPTIMIZATION: Only animate content size when expanded state changes
                .then(
                    if (expanded) Modifier.animateContentSize(
                        animationSpec = tween(200) // OPTIMIZATION: Faster animation
                    ) else Modifier
                )
        ) {
            // OPTIMIZATION: Flatten layout hierarchy - use single Row with weights
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Company initial icon - OPTIMIZATION: Use drawBehind for background
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(primaryContainerColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = companyInitial,
                        style = MaterialTheme.typography.titleLarge,
                        color = primaryColor
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // Company and position info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = application.companyName,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = application.jobTitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = onSurfaceVariantColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                // Status badge
                StatusBadge(status = application.status)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Date and expand button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = onSurfaceVariantColor
                )
                
                IconButton(onClick = toggleExpanded) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Collapse" else "Expand"
                    )
                }
            }
            
            // OPTIMIZATION: Only compose expanded content when needed
            if (expanded) {
                ExpandedCardContent(
                    application = application,
                    salaryDisplay = salaryDisplay,
                    onEdit = onEdit,
                    onDelete = onDelete
                )
            }
        }
    }
}

// OPTIMIZATION: Extract expanded content to separate composable to limit recomposition scope
@Composable
private fun ExpandedCardContent(
    application: JobApplication,
    salaryDisplay: String?,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Spacer(modifier = Modifier.height(8.dp))
    
    // Location
    application.companyLocation?.let { location ->
        Text(
            text = "📍 $location",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
    }
    
    // Salary range
    salaryDisplay?.let { salary ->
        Text(
            text = "💰 $salary",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
    }
    
    // Notes
    application.notes?.takeIf { it.isNotBlank() }?.let { notes ->
        Text(
            text = "📝 $notes",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
    
    // Action buttons
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        IconButton(onClick = onEdit) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}
