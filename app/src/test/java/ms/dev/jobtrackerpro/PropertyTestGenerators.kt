package ms.dev.jobtrackerpro

import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import ms.dev.jobtrackerpro.domain.model.*

/**
 * Custom Kotest generators for property-based testing.
 */
object PropertyTestGenerators {
    
    /**
     * Generator for ApplicationStatus enum.
     */
    val applicationStatusArb: Arb<ApplicationStatus> = Arb.enum<ApplicationStatus>()
    
    /**
     * Generator for JobType enum.
     */
    val jobTypeArb: Arb<JobType> = Arb.enum<JobType>()
    
    /**
     * Generator for RemoteStatus enum.
     */
    val remoteStatusArb: Arb<RemoteStatus> = Arb.enum<RemoteStatus>()
    
    /**
     * Generator for CompanySize enum.
     */
    val companySizeArb: Arb<CompanySize> = Arb.enum<CompanySize>()
    
    /**
     * Generator for SalaryRange.
     */
    val salaryRangeArb: Arb<SalaryRange> = arbitrary {
        val min = Arb.int(30000..100000).bind()
        val max = Arb.int(min..200000).bind()
        SalaryRange(min, max)
    }
    
    /**
     * Generator for Experience.
     */
    val experienceArb: Arb<Experience> = arbitrary {
        Experience(
            company = Arb.string(3..50).bind(),
            title = Arb.string(3..50).bind(),
            startDate = Arb.string(4..10).orNull().bind(),
            endDate = Arb.string(4..10).orNull().bind(),
            description = Arb.string(10..200).orNull().bind()
        )
    }
    
    /**
     * Generator for Education.
     */
    val educationArb: Arb<Education> = arbitrary {
        Education(
            institution = Arb.string(3..50).bind(),
            degree = Arb.element("Bachelor's", "Master's", "PhD", "Associate's", "B.S.", "M.S.").bind(),
            field = Arb.string(3..30).orNull().bind(),
            graduationDate = Arb.string(4..10).orNull().bind()
        )
    }
    
    /**
     * Generator for JobApplication.
     */
    val jobApplicationArb: Arb<JobApplication> = arbitrary {
        val created = Arb.long(1609459200000L..System.currentTimeMillis()).bind()
        JobApplication(
            id = Arb.long(1L..10000L).bind(),
            companyName = Arb.string(2..50).bind(),
            jobTitle = Arb.string(2..50).bind(),
            applicationDate = Arb.long(1609459200000L..System.currentTimeMillis()).bind(),
            status = applicationStatusArb.bind(),
            companyLocation = Arb.string(2..50).orNull().bind(),
            jobDescription = Arb.string(10..500).orNull().bind(),
            jobLink = Arb.string(10..100).orNull().bind(),
            salaryRange = salaryRangeArb.orNull().bind(),
            jobType = jobTypeArb.orNull().bind(),
            remoteStatus = remoteStatusArb.orNull().bind(),
            companySize = companySizeArb.orNull().bind(),
            industry = Arb.string(3..30).orNull().bind(),
            notes = Arb.string(0..200).orNull().bind(),
            rating = Arb.int(1..5).orNull().bind(),
            companyWebsite = Arb.string(5..50).orNull().bind(),
            createdTimestamp = created,
            updatedTimestamp = Arb.long(created..System.currentTimeMillis()).bind()
        )
    }

    
    /**
     * Generator for StatusHistory.
     */
    val statusHistoryArb: Arb<StatusHistory> = arbitrary {
        StatusHistory(
            id = Arb.long(1L..10000L).bind(),
            applicationId = Arb.long(1L..10000L).bind(),
            status = applicationStatusArb.bind(),
            statusDate = Arb.long(1609459200000L..System.currentTimeMillis()).bind(),
            notes = Arb.string(0..200).orNull().bind(),
            timestamp = Arb.long(1609459200000L..System.currentTimeMillis()).bind()
        )
    }
    
    /**
     * Generator for Communication.
     */
    val communicationArb: Arb<Communication> = arbitrary {
        Communication(
            id = Arb.long(1L..10000L).bind(),
            applicationId = Arb.long(1L..10000L).bind(),
            recruiterName = Arb.string(2..30).orNull().bind(),
            recruiterEmail = Arb.email().orNull().bind(),
            recruiterPhone = Arb.string(10..15).orNull().bind(),
            communicationType = Arb.enum<CommunicationType>().orNull().bind(),
            communicationDate = Arb.long(1609459200000L..System.currentTimeMillis()).orNull().bind(),
            communicationNotes = Arb.string(0..200).orNull().bind()
        )
    }
    
    /**
     * Generator for list of JobApplications.
     */
    fun jobApplicationListArb(size: IntRange = 0..20): Arb<List<JobApplication>> = 
        Arb.list(jobApplicationArb, size)
    
    /**
     * Generator for search query strings.
     */
    val searchQueryArb: Arb<String> = Arb.string(1..20)
    
    /**
     * Generator for date ranges.
     */
    val dateRangeArb: Arb<Pair<Long, Long>> = arbitrary {
        val start = Arb.long(1609459200000L..System.currentTimeMillis() - 86400000L).bind()
        val end = Arb.long(start..System.currentTimeMillis()).bind()
        Pair(start, end)
    }
}
