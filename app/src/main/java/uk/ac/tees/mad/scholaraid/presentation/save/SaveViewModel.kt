package uk.ac.tees.mad.scholaraid.presentation.save

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uk.ac.tees.mad.scholaraid.data.model.Scholarship
import uk.ac.tees.mad.scholaraid.domain.repository.ScholarshipRepository
import uk.ac.tees.mad.scholaraid.domain.repository.UserRepository
import uk.ac.tees.mad.scholaraid.domain.repository.LocalScholarshipRepository
import uk.ac.tees.mad.scholaraid.util.Resource
import javax.inject.Inject

data class SaveScreenState(
    val isLoading: Boolean = false,
    val scholarships: List<Scholarship> = emptyList(),
    val error: String? = null,
    val isOffline: Boolean = false
)

@HiltViewModel
class SaveViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val scholarshipRepository: ScholarshipRepository,
    private val localScholarshipRepository: LocalScholarshipRepository,
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
            // If user is not logged in, show only local saved scholarships
            loadLocalScholarships()
            return
        }

        viewModelScope.launch {
            // First, load from local database for immediate offline support
            loadLocalScholarships()

            // Then try to sync with remote
            userRepository.getSavedScholarshipIds(userId)
                .onEach { idResult ->
                    when (idResult) {
                        is Resource.Loading -> {
                            _state.update { it.copy(isLoading = true) }
                        }
                        is Resource.Error -> {
                            // If error, we're probably offline - show local data with offline indicator
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    error = "Using offline data. ${idResult.message}",
                                    isOffline = true
                                )
                            }
                        }
                        is Resource.Success -> {
                            val remoteIds = idResult.data ?: emptyList()

                            if (remoteIds.isEmpty()) {
                                // No remote scholarships, clear local cache if needed
                                _state.update {
                                    it.copy(
                                        isLoading = false,
                                        scholarships = emptyList(),
                                        error = null,
                                        isOffline = false
                                    )
                                }
                            } else {
                                // Fetch full scholarship objects and update local cache
                                try {
                                    val remoteScholarships = scholarshipRepository.getScholarshipsByIds(remoteIds)

                                    // Update local database with fresh data
                                    updateLocalCache(remoteScholarships)

                                    _state.update {
                                        it.copy(
                                            isLoading = false,
                                            scholarships = remoteScholarships,
                                            error = null,
                                            isOffline = false
                                        )
                                    }
                                } catch (e: Exception) {
                                    // If fetching scholarships fails, use local data with offline indicator
                                    _state.update {
                                        it.copy(
                                            isLoading = false,
                                            error = "Using offline data. Could not sync with server.",
                                            isOffline = true
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    private fun loadLocalScholarships() {
        viewModelScope.launch {
            localScholarshipRepository.getAllSavedScholarships()
                .collect { localScholarships ->
                    _state.update {
                        it.copy(
                            scholarships = localScholarships,
                            isLoading = false
                        )
                    }
                }
        }
    }

    private suspend fun updateLocalCache(scholarships: List<Scholarship>) {
        // Clear existing cache and save new data
        localScholarshipRepository.clearAll()
        scholarships.forEach { scholarship ->
            localScholarshipRepository.saveScholarship(scholarship)
        }
    }

    // Call this when user saves/unsaves a scholarship to keep cache updated
    fun onScholarshipSaved(scholarship: Scholarship) {
        viewModelScope.launch {
            localScholarshipRepository.saveScholarship(scholarship)
        }
    }

    fun onScholarshipUnsaved(scholarshipId: String) {
        viewModelScope.launch {
            localScholarshipRepository.unsaveScholarship(scholarshipId)
        }
    }

    fun retrySync() {
        getSavedScholarships()
    }
}