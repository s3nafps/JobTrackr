package ms.dev.jobtrackerpro.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Samsung One Sans fallback to Roboto (system default)
val SamsungOneSans = FontFamily.Default

// OneUI 8.5 Typography Scale
val Typography = Typography(
    // Heading H1: 28sp, Bold
    displayLarge = TextStyle(
        fontFamily = SamsungOneSans,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        letterSpacing = (-0.01).sp
    ),
    // Heading H2: 24sp, Semi-bold
    displayMedium = TextStyle(
        fontFamily = SamsungOneSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        letterSpacing = 0.sp
    ),
    // Heading H3: 20sp, Semi-bold
    displaySmall = TextStyle(
        fontFamily = SamsungOneSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        letterSpacing = 0.sp
    ),
    // Title Large: 22sp, Medium
    headlineLarge = TextStyle(
        fontFamily = SamsungOneSans,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        letterSpacing = 0.sp
    ),
    // Title Medium: 18sp, Medium
    headlineMedium = TextStyle(
        fontFamily = SamsungOneSans,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        letterSpacing = 0.sp
    ),
    // Title Small: 16sp, Medium
    headlineSmall = TextStyle(
        fontFamily = SamsungOneSans,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        letterSpacing = 0.sp
    ),
    // Body Large: 16sp, Regular
    bodyLarge = TextStyle(
        fontFamily = SamsungOneSans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.sp
    ),
    // Body Primary: 14sp, Regular
    bodyMedium = TextStyle(
        fontFamily = SamsungOneSans,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.sp
    ),
    // Body Secondary: 12sp, Regular
    bodySmall = TextStyle(
        fontFamily = SamsungOneSans,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        letterSpacing = 0.sp
    ),
    // Label Large: 14sp, Medium
    labelLarge = TextStyle(
        fontFamily = SamsungOneSans,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 0.sp
    ),
    // Label Medium: 12sp, Medium
    labelMedium = TextStyle(
        fontFamily = SamsungOneSans,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        letterSpacing = 0.sp
    ),
    // Label Small: 10sp, Medium
    labelSmall = TextStyle(
        fontFamily = SamsungOneSans,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        letterSpacing = 0.sp
    ),
    // Title Large
    titleLarge = TextStyle(
        fontFamily = SamsungOneSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        letterSpacing = 0.sp
    ),
    // Title Medium
    titleMedium = TextStyle(
        fontFamily = SamsungOneSans,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        letterSpacing = 0.sp
    ),
    // Title Small
    titleSmall = TextStyle(
        fontFamily = SamsungOneSans,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 0.sp
    )
)
