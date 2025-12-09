package ms.dev.jobtrackerpro.domain.model

/**
 * Domain model for a parsed resume/CV.
 */
data class ParsedResume(
    val id: Long = 0,
    val fullName: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val location: String? = null,
    val summary: String? = null,
    val skills: List<String> = emptyList(),
    val experiences: List<Experience> = emptyList(),
    val education: List<Education> = emptyList(),
    val certifications: List<String> = emptyList(),
    val atsScore: Int? = null,
    val parsedTimestamp: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)

/**
 * Work experience data class.
 */
data class Experience(
    val company: String,
    val title: String,
    val startDate: String? = null,
    val endDate: String? = null,
    val description: String? = null
)

/**
 * Education data class.
 */
data class Education(
    val institution: String,
    val degree: String,
    val field: String? = null,
    val graduationDate: String? = null
)

/**
 * ATS Score breakdown.
 */
data class ATSScore(
    val overallScore: Int,
    val formattingScore: Int,
    val keywordScore: Int,
    val experienceScore: Int,
    val educationScore: Int,
    val skillsScore: Int,
    val compatibilityScore: Int
) {
    fun getScoreColor(): ScoreColor {
        return when {
            overallScore <= 30 -> ScoreColor.RED
            overallScore <= 60 -> ScoreColor.ORANGE
            overallScore <= 80 -> ScoreColor.YELLOW
            else -> ScoreColor.GREEN
        }
    }
}

enum class ScoreColor {
    RED, ORANGE, YELLOW, GREEN
}

/**
 * Improvement suggestion for ATS score.
 */
data class Suggestion(
    val category: String,
    val message: String,
    val impact: Int // 1-5, higher is more impactful
)

/**
 * Job match result when comparing CV to job description.
 */
data class MatchResult(
    val matchPercentage: Int,
    val matchingSkills: List<String>,
    val missingSkills: List<String>
)
