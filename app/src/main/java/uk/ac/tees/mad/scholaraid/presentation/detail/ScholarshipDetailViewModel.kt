package uk.ac.tees.mad.scholaraid.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import uk.ac.tees.mad.scholaraid.data.model.Scholarship
import uk.ac.tees.mad.scholaraid.domain.repository.ScholarshipRepository
import uk.ac.tees.mad.scholaraid.domain.repository.UserRepository
import uk.ac.tees.mad.scholaraid.domain.repository.LocalScholarshipRepository
import uk.ac.tees.mad.scholaraid.util.Resource
import javax.inject.Inject

@HiltViewModel
class ScholarshipDetailViewModel @Inject constructor(
    private val scholarshipRepository: ScholarshipRepository,
    private val userRepository: UserRepository,
    private val localScholarshipRepository: LocalScholarshipRepository,
    private val firebaseAuth: FirebaseAuth,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val scholarshipId: String = savedStateHandle.get<String>("scholarshipId")
        ?: throw IllegalArgumentException("Scholarship ID not found")

    private val _scholarship = MutableStateFlow<Scholarship?>(null)
    val scholarship: StateFlow<Scholarship?> = _scholarship.asStateFlow()

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    private val currentUserId: String?
        get() = firebaseAuth.currentUser?.uid

    init {
        fetchScholarshipDetail()
        checkIfSaved()
    }

    private fun fetchScholarshipDetail() {
        viewModelScope.launch {
            // Try to get from remote first, fall back to local if needed
            val remoteScholarship = scholarshipRepository.getScholarshipById(scholarshipId)
            if (remoteScholarship != null) {
                _scholarship.value = remoteScholarship
            } else {
                // Try to get from local cache
                val localScholarship = localScholarshipRepository.isScholarshipSaved(scholarshipId)
                // Note: We don't store full scholarship data locally unless it's saved
                // So we might not have the full details locally
            }
        }
    }

    private fun checkIfSaved() {
        val userId = currentUserId

        if (userId != null) {
            // Check remote first
            userRepository.getSavedScholarshipIds(userId)
                .onEach { result ->
                    if (result is Resource.Success) {
                        _isSaved.value = result.data?.contains(scholarshipId) == true
                    }
                }
                .launchIn(viewModelScope)
        } else {
            // If not logged in, check local cache
            viewModelScope.launch {
                _isSaved.value = localScholarshipRepository.isScholarshipSaved(scholarshipId)
            }
        }
    }

    fun toggleSaveStatus() {
        viewModelScope.launch {
            val userId = currentUserId
            val currentlySaved = _isSaved.value
            val currentScholarship = _scholarship.value

            if (currentlySaved) {
                // Unsave
                if (userId != null) {
                    userRepository.unsaveScholarship(userId, scholarshipId)
                }
                localScholarshipRepository.unsaveScholarship(scholarshipId)
                _isSaved.value = false
            } else {
                // Save
                if (userId != null) {
                    userRepository.saveScholarship(userId, scholarshipId)
                }
                // Save to local cache
                currentScholarship?.let {
                    localScholarshipRepository.saveScholarship(it)
                }
                _isSaved.value = true
            }
        }
    }
}