package ms.dev.jobtrackerpro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import ms.dev.jobtrackerpro.presentation.navigation.JobTrackerNavHost
import ms.dev.jobtrackerpro.ui.theme.JobtrackerProTheme

/**
 * Main Activity optimized for fast cold start.
 * 
 * COLD START OPTIMIZATIONS:
 * 1. Splash screen installed before super.onCreate() to show immediately
 * 2. enableEdgeToEdge() called early for seamless transition
 * 3. Minimal work before setContent() - UI renders ASAP
 * 4. No heavy initialization in onCreate()
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        // OPTIMIZATION: Install splash screen BEFORE super.onCreate()
        // This ensures the splash shows immediately during cold start
        val splashScreen = installSplashScreen()
        
        super.onCreate(savedInstanceState)
        
        // OPTIMIZATION: Enable edge-to-edge early for seamless splash -> content transition
        enableEdgeToEdge()
        
        // OPTIMIZATION: Set content immediately - no blocking operations before this
        setContent {
            JobtrackerProTheme {
                JobTrackerNavHost()
            }
        }
    }
}
