package ms.dev.jobtrackerpro.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ms.dev.jobtrackerpro.presentation.analytics.AnalyticsScreen
import ms.dev.jobtrackerpro.presentation.applications.ApplicationsListScreen
import ms.dev.jobtrackerpro.presentation.dashboard.DashboardScreen
import ms.dev.jobtrackerpro.presentation.detail.ApplicationDetailScreen
import ms.dev.jobtrackerpro.presentation.settings.SettingsScreen
import ms.dev.jobtrackerpro.ui.theme.*

sealed class Screen(
    val route: String, 
    val title: String, 
    val selectedIcon: ImageVector? = null,
    val unselectedIcon: ImageVector? = null
) {
    object Dashboard : Screen("dashboard", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    object Applications : Screen("applications", "Jobs", Icons.Filled.WorkOutline, Icons.Outlined.WorkOutline)
    object AddApplication : Screen("add_application", "Add Application")
    object ApplicationDetail : Screen("application/{applicationId}", "Application Detail") {
        fun createRoute(applicationId: Long) = "application/$applicationId"
    }
    object Analytics : Screen("analytics", "Stats", Icons.Filled.BarChart, Icons.Outlined.BarChart)
    object Settings : Screen("settings", "Settings")
}

val bottomNavItems = listOf(
    Screen.Dashboard,
    Screen.Applications,
    Screen.Analytics
)

@Composable
fun JobTrackerNavHost(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Hide bottom bar on detail screens
    val showBottomBar = currentRoute in listOf(
        Screen.Dashboard.route,
        Screen.Applications.route,
        Screen.Analytics.route
    )
    
    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onNavigateToApplications = {
                        navController.navigate(Screen.Applications.route)
                    },
                    onNavigateToAddApplication = {
                        navController.navigate(Screen.AddApplication.route)
                    },
                    onNavigateToAnalytics = {
                        navController.navigate(Screen.Analytics.route)
                    },
                    onNavigateToSettings = {
                        navController.navigate(Screen.Settings.route)
                    },
                    onNavigateToApplicationDetail = { applicationId ->
                        navController.navigate(Screen.ApplicationDetail.createRoute(applicationId))
                    }
                )
            }
            
            composable(Screen.Applications.route) {
                ApplicationsListScreen(
                    onNavigateToAddApplication = {
                        navController.navigate(Screen.AddApplication.route)
                    },
                    onNavigateToApplicationDetail = { applicationId ->
                        navController.navigate(Screen.ApplicationDetail.createRoute(applicationId))
                    }
                )
            }
            
            composable(Screen.AddApplication.route) {
                ApplicationDetailScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(
                route = Screen.ApplicationDetail.route,
                arguments = listOf(
                    navArgument("applicationId") { type = NavType.LongType }
                )
            ) {
                ApplicationDetailScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(Screen.Analytics.route) {
                AnalyticsScreen()
            }
            
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

// Vibrant colors for each nav item
private val navItemColors = listOf(
    PrimaryBlue to PrimaryBlue.copy(alpha = 0.15f),
    SecondaryPurple to SecondaryPurple.copy(alpha = 0.15f),
    AccentTeal to AccentTeal.copy(alpha = 0.15f)
)

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomNavItems.forEachIndexed { index, screen ->
                val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                val (activeColor, bgColor) = navItemColors.getOrElse(index) { 
                    PrimaryBlue to PrimaryBlue.copy(alpha = 0.15f) 
                }
                
                AnimatedNavItem(
                    screen = screen,
                    isSelected = isSelected,
                    activeColor = activeColor,
                    activeBgColor = bgColor,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun AnimatedNavItem(
    screen: Screen,
    isSelected: Boolean,
    activeColor: Color,
    activeBgColor: Color,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) activeBgColor else Color.Transparent,
        animationSpec = tween(300),
        label = "backgroundColor"
    )
    
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) activeColor else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(300),
        label = "iconColor"
    )
    
    val textColor by animateColorAsState(
        targetValue = if (isSelected) activeColor else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(300),
        label = "textColor"
    )
    
    Box(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val icon = if (isSelected) screen.selectedIcon else screen.unselectedIcon
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = screen.title,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            AnimatedVisibility(
                visible = isSelected,
                enter = fadeIn(animationSpec = tween(300)) + 
                        expandHorizontally(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(300)) + 
                       shrinkHorizontally(animationSpec = tween(300))
            ) {
                Text(
                    text = screen.title,
                    style = MaterialTheme.typography.labelMedium,
                    color = textColor
                )
            }
        }
    }
}
