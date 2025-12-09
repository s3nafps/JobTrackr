package ms.dev.jobtrackerpro.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Date utility functions.
 */
object DateUtils {
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    
    fun formatDate(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }
    
    fun formatDateTime(timestamp: Long): String {
        return dateTimeFormat.format(Date(timestamp))
    }
    
    fun getCurrentTimestamp(): Long = System.currentTimeMillis()
    
    fun getStartOfDay(timestamp: Long): Long {
        val date = Date(timestamp)
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .parse(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date))
            ?.time ?: timestamp
    }
}
