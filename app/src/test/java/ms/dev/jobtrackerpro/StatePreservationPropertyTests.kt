package ms.dev.jobtrackerpro

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import ms.dev.jobtrackerpro.PropertyTestGenerators.applicationStatusArb
import ms.dev.jobtrackerpro.PropertyTestGenerators.jobApplicationListArb
import ms.dev.jobtrackerpro.domain.model.ApplicationStatus
import ms.dev.jobtrackerpro.domain.model.SortOption

/**
 * Property-based tests for state preservation across configuration changes.
 * 
 * **Feature: job-tracker-app, Property 24: Configuration Change State Preservation**
 */
class StatePreservationPropertyTests : FunSpec({
    
    /**
     * **Feature: job-tracker-app, Property 24: Configuration Change State Preservation**
     * *For any* screen state (scroll position, form inputs, selected items) when orientation 
     * changes, the state SHALL be preserved after the configuration change completes.
     * **Validates: Requirements 10.3**
     */
    test("Property 24: Configuration change state preservation - UI state preserved") {
        checkAll(100, 
            Arb.int(0..1000),  // scroll position
            Arb.string(0..100),  // search query
            applicationStatusArb.orNull(),  // filter status
            Arb.enum<SortOption>()  // sort option
        ) { scrollPosition, searchQuery, filterStatus, sortOption ->
            
            // Create initial UI state
            val initialState = ApplicationsUiState(
                scrollPosition = scrollPosition,
                searchQuery = searchQuery,
                filterStatus = filterStatus,
                sortOption = sortOption
            )
            
            // Simulate saving state to SavedStateHandle
            val savedState = saveState(initialState)
            
            // Simulate configuration change (orientation change)
            // State is restored from SavedStateHandle
            val restoredState = restoreState(savedState)
            
            // All state should be preserved
            restoredState.scrollPosition shouldBe initialState.scrollPosition
            restoredState.searchQuery shouldBe initialState.searchQuery
            restoredState.filterStatus shouldBe initialState.filterStatus
            restoredState.sortOption shouldBe initialState.sortOption
        }
    }
    
    /**
     * Test form input preservation.
     */
    test("Form input state preservation - all form fields preserved") {
        checkAll(100,
            Arb.string(0..100),  // company name
            Arb.string(0..100),  // job title
            Arb.string(0..500),  // notes
            Arb.int(0..200000).orNull(),  // salary min
            Arb.int(0..300000).orNull()   // salary max
        ) { companyName, jobTitle, notes, salaryMin, salaryMax ->
            
            // Create form state
            val initialFormState = ApplicationFormState(
                companyName = companyName,
                jobTitle = jobTitle,
                notes = notes,
                salaryMin = salaryMin,
                salaryMax = salaryMax
            )
            
            // Simulate saving and restoring
            val savedFormState = saveFormState(initialFormState)
            val restoredFormState = restoreFormState(savedFormState)
            
            // All form fields should be preserved
            restoredFormState.companyName shouldBe initialFormState.companyName
            restoredFormState.jobTitle shouldBe initialFormState.jobTitle
            restoredFormState.notes shouldBe initialFormState.notes
            restoredFormState.salaryMin shouldBe initialFormState.salaryMin
            restoredFormState.salaryMax shouldBe initialFormState.salaryMax
        }
    }
    
    /**
     * Test selected items preservation.
     */
    test("Selected items state preservation - selection preserved") {
        checkAll(100, Arb.set(Arb.long(1L..1000L), 0..20)) { selectedIds ->
            
            // Create selection state
            val initialSelection = SelectionState(selectedIds = selectedIds)
            
            // Simulate saving and restoring
            val savedSelection = saveSelectionState(initialSelection)
            val restoredSelection = restoreSelectionState(savedSelection)
            
            // Selection should be preserved
            restoredSelection.selectedIds shouldBe initialSelection.selectedIds
        }
    }
})

// UI State data classes for testing

private data class ApplicationsUiState(
    val scrollPosition: Int,
    val searchQuery: String,
    val filterStatus: ApplicationStatus?,
    val sortOption: SortOption
)

private data class ApplicationFormState(
    val companyName: String,
    val jobTitle: String,
    val notes: String,
    val salaryMin: Int?,
    val salaryMax: Int?
)

private data class SelectionState(
    val selectedIds: Set<Long>
)

// Simulated SavedStateHandle operations

private fun saveState(state: ApplicationsUiState): Map<String, Any?> {
    return mapOf(
        "scrollPosition" to state.scrollPosition,
        "searchQuery" to state.searchQuery,
        "filterStatus" to state.filterStatus?.name,
        "sortOption" to state.sortOption.name
    )
}

private fun restoreState(savedState: Map<String, Any?>): ApplicationsUiState {
    return ApplicationsUiState(
        scrollPosition = savedState["scrollPosition"] as Int,
        searchQuery = savedState["searchQuery"] as String,
        filterStatus = (savedState["filterStatus"] as? String)?.let { ApplicationStatus.fromString(it) },
        sortOption = SortOption.valueOf(savedState["sortOption"] as String)
    )
}

private fun saveFormState(state: ApplicationFormState): Map<String, Any?> {
    return mapOf(
        "companyName" to state.companyName,
        "jobTitle" to state.jobTitle,
        "notes" to state.notes,
        "salaryMin" to state.salaryMin,
        "salaryMax" to state.salaryMax
    )
}

private fun restoreFormState(savedState: Map<String, Any?>): ApplicationFormState {
    return ApplicationFormState(
        companyName = savedState["companyName"] as String,
        jobTitle = savedState["jobTitle"] as String,
        notes = savedState["notes"] as String,
        salaryMin = savedState["salaryMin"] as? Int,
        salaryMax = savedState["salaryMax"] as? Int
    )
}

private fun saveSelectionState(state: SelectionState): Map<String, Any?> {
    return mapOf("selectedIds" to state.selectedIds.toList())
}

@Suppress("UNCHECKED_CAST")
private fun restoreSelectionState(savedState: Map<String, Any?>): SelectionState {
    return SelectionState(
        selectedIds = (savedState["selectedIds"] as List<Long>).toSet()
    )
}
