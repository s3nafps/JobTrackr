package ms.dev.jobtrackerpro.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ms.dev.jobtrackerpro.domain.model.ApplicationStatus
import ms.dev.jobtrackerpro.ui.theme.*

@Composable
fun StatusBadge(
    status: ApplicationStatus,
    modifier: Modifier = Modifier
) {
    val statusColor = getStatusColor(status)

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(statusColor.copy(alpha = 0.15f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Colored dot indicator
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(statusColor)
        )

        Text(
            text = status.displayName,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = statusColor
        )
    }
}

@Composable
fun getStatusColor(status: ApplicationStatus): Color {
    return when (status) {
        ApplicationStatus.APPLIED -> StatusApplied
        ApplicationStatus.EMAIL -> StatusEmail
        ApplicationStatus.PHONE -> StatusPhone
        ApplicationStatus.INTERVIEW -> StatusInterview
        ApplicationStatus.OFFER -> StatusOffer
        ApplicationStatus.REJECTED_BY_COMPANY -> StatusRejected
        ApplicationStatus.REJECTED_BY_ME -> StatusRejected
        ApplicationStatus.GHOSTED -> StatusGhosted
    }
}


