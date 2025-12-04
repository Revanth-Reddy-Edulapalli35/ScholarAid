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
import uk.ac.tees.mad.scholaraid.util.Resource
import javax.inject.Inject

@HiltViewModel
class ScholarshipDetailViewModel @Inject constructor(
    private val scholarshipRepository: ScholarshipRepository,
    private val userRepository: UserRepository, // Injected UserRepository
    private val firebaseAuth: FirebaseAuth, // Injected FirebaseAuth
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
            _scholarship.value = scholarshipRepository.getScholarshipById(scholarshipId)
        }
    }

    private fun checkIfSaved() {
        val userId = currentUserId ?: return

        userRepository.getSavedScholarshipIds(userId)
            .onEach { result ->
                if (result is Resource.Success) {
                    _isSaved.value = result.data?.contains(scholarshipId) == true
                }
            }
            .launchIn(viewModelScope)
    }

    fun toggleSaveStatus() {
        viewModelScope.launch {
            val userId = currentUserId ?: return@launch
            val currentlySaved = _isSaved.value

            val result = if (currentlySaved) {
                userRepository.unsaveScholarship(userId, scholarshipId)
            } else {
                userRepository.saveScholarship(userId, scholarshipId)
            }

            // The 'checkIfSaved' flow will automatically update the UI
            // but we can preemptively toggle it for a faster UI response
            if (result is Resource.Success) {
                _isSaved.value = !currentlySaved
            }
            // In a real app, you'd show a snackbar on Resource.Error
        }
    }
}