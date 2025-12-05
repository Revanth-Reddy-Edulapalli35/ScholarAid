package uk.ac.tees.mad.scholaraid.presentation.save

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import uk.ac.tees.mad.scholaraid.data.model.Scholarship
import uk.ac.tees.mad.scholaraid.domain.repository.ScholarshipRepository
import uk.ac.tees.mad.scholaraid.domain.repository.UserRepository
import uk.ac.tees.mad.scholaraid.util.Resource
import javax.inject.Inject

data class SaveScreenState(
    val isLoading: Boolean = false,
    val scholarships: List<Scholarship> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class SaveViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val scholarshipRepository: ScholarshipRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _state = MutableStateFlow(SaveScreenState())
    val state: StateFlow<SaveScreenState> = _state.asStateFlow()

    private val currentUserId: String?
        get() = firebaseAuth.currentUser?.uid

    init {
        getSavedScholarships()
    }

    private fun getSavedScholarships() {
        val userId = currentUserId
        if (userId == null) {
            _state.value = SaveScreenState(error = "User not logged in")
            return
        }

        viewModelScope.launch {
            _state.value = SaveScreenState(isLoading = true)

            try {
                userRepository.getSavedScholarshipIds(userId)
                    .catch { e ->
                        _state.value = SaveScreenState(error = e.message ?: "Failed to load saved scholarships")
                    }
                    .collect { idResult ->
                        when (idResult) {
                            is Resource.Loading -> {
                                _state.value = SaveScreenState(isLoading = true)
                            }
                            is Resource.Error -> {
                                _state.value = SaveScreenState(error = idResult.message ?: "Failed to load saved scholarships")
                            }
                            is Resource.Success -> {
                                val ids = idResult.data ?: emptyList()
                                if (ids.isEmpty()) {
                                    _state.value = SaveScreenState(scholarships = emptyList())
                                } else {
                                    // Fetch the full scholarship objects
                                    try {
                                        val scholarships = scholarshipRepository.getScholarshipsByIds(ids)
                                        _state.value = SaveScreenState(scholarships = scholarships)
                                    } catch (e: Exception) {
                                        _state.value = SaveScreenState(error = e.message ?: "Failed to load scholarship details")
                                    }
                                }
                            }
                        }
                    }
            } catch (e: Exception) {
                _state.value = SaveScreenState(error = e.message ?: "An unexpected error occurred")
            }
        }
    }
}