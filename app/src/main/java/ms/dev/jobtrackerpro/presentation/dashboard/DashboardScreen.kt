package ms.dev.jobtrackerpro.presentation.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ms.dev.jobtrackerpro.presentation.components.*
import ms.dev.jobtrackerpro.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToApplications: () -> Unit,
    onNavigateToAddApplication: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToApplicationDetail: (Long) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            UnifiedTopBar(
                title = getGreeting(),
                subtitle = getCurrentDate(),
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            AnimatedFAB(onClick = onNavigateToAddApplication)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            // Quick Stats Cards with gradient
            QuickStatsSection(uiState.statistics, uiState.isLoading)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Recent Applications Section
            SectionHeader(
                title = "Recent Applications",
                action = "View All",
                onActionClick = onNavigateToApplications
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (uiState.isLoading) {
                RecentApplicationsSkeleton()
            } else if (uiState.recentApplications.isEmpty()) {
                EmptyState(
                    emoji = "📝",
                    title = "No applications yet",
                    subtitle = "Start tracking your job search",
                    actionLabel = "Add Application",
                    onAction = onNavigateToAddApplication,
                    modifier = Modifier.padding(vertical = 32.dp)
                )
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    itemsIndexed(uiState.recentApplications) { index, application ->
                        AnimatedRecentApplicationCard(
                            application = application,
                            onClick = { onNavigateToApplicationDetail(application.id) },
                            delay = 300 + (index * 80)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
private fun QuickStatsSection(
    statistics: ms.dev.jobtrackerpro.domain.model.DashboardStatistics,
    isLoading: Boolean
) {
    if (isLoading) {
        StatisticsGridSkeleton()
        return
    }
    
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Main stat card with gradient
        AnimatedStatCard(
            title = "Total Applications",
            value = statistics.totalApplications.toString(),
            gradientColors = GradientBlue,
            delay = 0,
            modifier = Modifier.fillMaxWidth()
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SmallStatCard(
                title = "Responses",
                value = statistics.responsesReceived.toString(),
                accentColor = AccentPurple,
                delay = 100,
                modifier = Modifier.weight(1f)
            )
            SmallStatCard(
                title = "Interviews",
                value = statistics.interviewsScheduled.toString(),
                accentColor = StatusInterview,
                delay = 150,
                modifier = Modifier.weight(1f)
            )
            SmallStatCard(
                title = "Offers",
                value = statistics.offersReceived.toString(),
                accentColor = StatusOffer,
                delay = 200,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// OPTIMIZATION: Use key parameter to prevent unnecessary recomposition when delay changes
@Composable
private fun AnimatedStatCard(
    title: String,
    value: String,
    gradientColors: List<Color>,
    delay: Int,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    
    // OPTIMIZATION: Key on delay to prevent re-triggering animation unnecessarily
    LaunchedEffect(delay) {
        kotlinx.coroutines.delay(delay.toLong())
        visible = true
    }
    
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.9f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(300),
        label = "alpha"
    )
    
    Card(
        modifier = modifier
            .scale(scale)
            .alpha(alpha),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(gradientColors))
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

// OPTIMIZATION: Use key parameter to prevent unnecessary recomposition
@Composable
private fun SmallStatCard(
    title: String,
    value: String,
    accentColor: Color,
    delay: Int,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    
    // OPTIMIZATION: Key on delay to prevent re-triggering animation unnecessarily
    LaunchedEffect(delay) {
        kotlinx.coroutines.delay(delay.toLong())
        visible = true
    }
    
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.9f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(300),
        label = "alpha"
    )
    
    Card(
        modifier = modifier
            .scale(scale)
            .alpha(alpha),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(accentColor)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StatisticsGridSkeleton() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        StatisticsCardSkeleton(modifier = Modifier.fillMaxWidth().height(100.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatisticsCardSkeleton(modifier = Modifier.weight(1f).height(90.dp))
            StatisticsCardSkeleton(modifier = Modifier.weight(1f).height(90.dp))
            StatisticsCardSkeleton(modifier = Modifier.weight(1f).height(90.dp))
        }
    }
}

@Composable
private fun RecentApplicationsSkeleton() {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(3) {
            ApplicationCardSkeleton(modifier = Modifier.width(280.dp))
        }
    }
}

// OPTIMIZATION: Use stable keys and remember callbacks to prevent unnecessary recompositions
@Composable
private fun AnimatedRecentApplicationCard(
    application: ms.dev.jobtrackerpro.domain.model.JobApplication,
    onClick: () -> Unit,
    delay: Int
) {
    var visible by remember { mutableStateOf(false) }
    
    // OPTIMIZATION: Key on application.id to prevent re-triggering when list recomposes
    LaunchedEffect(application.id) {
        kotlinx.coroutines.delay(delay.toLong())
        visible = true
    }
    
    val offsetX by animateIntAsState(
        targetValue = if (visible) 0 else 80,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "offsetX"
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(300),
        label = "alpha"
    )
    
    // OPTIMIZATION: Remember empty lambdas to prevent recomposition
    val emptyLambda = remember { {} }
    
    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX, 0) }
            .alpha(alpha)
    ) {
        ApplicationCard(
            application = application,
            onClick = onClick,
            onEdit = emptyLambda,
            onDelete = emptyLambda,
            modifier = Modifier.width(280.dp)
        )
    }
}

@Composable
private fun AnimatedFAB(onClick: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(400)
        visible = true
    }
    
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "fabScale"
    )
    
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primary,
        modifier = Modifier.scale(scale)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Application",
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

private fun getGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> "Good Morning"
        hour < 17 -> "Good Afternoon"
        else -> "Good Evening"
    }
}

private fun getCurrentDate(): String {
    val dateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
    return dateFormat.format(Date())
}

val AccentPurple = Color(0xFF7C3AED)
