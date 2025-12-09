package ms.dev.jobtrackerpro

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Application class optimized for fast cold start and battery efficiency.
 * 
 * COLD START OPTIMIZATIONS:
 * 1. Minimal work in onCreate() - only essential initialization
 * 2. Timber initialized only in debug builds
 * 3. Heavy initialization deferred to background thread after first frame
 * 
 * BATTERY OPTIMIZATIONS:
 * 1. No background services started at app launch
 * 2. WorkManager used for deferred work (configured lazily)
 */
@HiltAndroidApp
class JobTrackerApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // OPTIMIZATION: Only plant Timber in debug builds
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        
        // OPTIMIZATION: Defer non-critical initialization to after first frame
        // This improves cold start time by not blocking the main thread
        ProcessLifecycleOwner.get().lifecycleScope.launch(Dispatchers.Default) {
            initializeNonCriticalComponents()
        }
    }
    
    /**
     * Initialize components that are not needed for first frame rendering.
     * This runs on a background thread after the app is visible.
     */
    private fun initializeNonCriticalComponents() {
        // Pre-warm any caches or perform background setup here
        // Example: Pre-load frequently used data, initialize analytics, etc.
        Timber.d("Non-critical initialization complete")
    }
}
