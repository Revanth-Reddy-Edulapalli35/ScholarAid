package uk.ac.tees.mad.scholaraid.presentation.scholarship

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uk.ac.tees.mad.scholaraid.data.model.Scholarship
import uk.ac.tees.mad.scholaraid.domain.repository.ScholarshipRepository
import javax.inject.Inject

// Data class to hold the entire UI state
data class ScholarshipUiState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val selectedCountry: String = "All",
    val availableCountries: List<String> = listOf("All"),
    val filteredScholarships: List<Scholarship> = emptyList()
)

@HiltViewModel
class ScholarshipViewModel @Inject constructor(
    private val scholarshipRepository: ScholarshipRepository
) : ViewModel() {

    // Private state
    private val _allScholarships = MutableStateFlow<List<Scholarship>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    private val _selectedCountry = MutableStateFlow("All") // "All" is the default
    private val _isLoading = MutableStateFlow(false)

    // Public UI state
    val uiState: StateFlow<ScholarshipUiState> = combine(
        _isLoading,
        _searchQuery,
        _selectedCountry,
        _allScholarships
    ) { isLoading, query, country, allScholarships ->

        // Get available countries from the full list
        val countries = listOf("All") + allScholarships
            .map { it.country }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()

        // Apply filters
        val filteredList = allScholarships.filter { scholarship ->
            // Search filter (checks title and provider)
            val matchesSearch = query.isBlank() ||
                    scholarship.title.contains(query, ignoreCase = true) ||
                    scholarship.provider.contains(query, ignoreCase = true)

            // Country filter
            val matchesCountry = country == "All" || scholarship.country == country

            matchesSearch && matchesCountry
        }

        ScholarshipUiState(
            isLoading = isLoading,
            searchQuery = query,
            selectedCountry = country,
            availableCountries = countries,
            filteredScholarships = filteredList
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ScholarshipUiState(isLoading = true) // Start in loading state
    )

    init {
        fetchScholarships()
    }

    private fun fetchScholarships() {
        viewModelScope.launch {
            _isLoading.value = true
            _allScholarships.value = scholarshipRepository.getScholarships()
            _isLoading.value = false
        }
    }

    // Public functions to update state from the UI
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onCountrySelected(country: String) {
        _selectedCountry.value = country
    }
}