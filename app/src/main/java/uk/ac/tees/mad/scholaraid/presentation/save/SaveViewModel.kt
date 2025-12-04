package uk.ac.tees.mad.scholaraid.presentation.save

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
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

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getSavedScholarships() {
        val userId = currentUserId
        if (userId == null) {
            _state.value = SaveScreenState(error = "User not logged in")
            return
        }

        viewModelScope.launch {
            userRepository.getSavedScholarshipIds(userId)
                .flatMapLatest { idResult ->
                    when (idResult) {
                        is Resource.Loading -> MutableStateFlow(SaveScreenState(isLoading = true))
                        is Resource.Error -> MutableStateFlow(SaveScreenState(error = idResult.message))
                        is Resource.Success -> {
                            val ids = idResult.data ?: emptyList()
                            if (ids.isEmpty()) {
                                MutableStateFlow(SaveScreenState(scholarships = emptyList()))
                            } else {
                                // Fetch the full scholarship objects
                                val scholarships = scholarshipRepository.getScholarshipsByIds(ids)
                                MutableStateFlow(SaveScreenState(scholarships = scholarships))
                            }
                        }
                    }
                }
                .collect { newState ->
                    _state.value = newState
                }
        }
    }
}