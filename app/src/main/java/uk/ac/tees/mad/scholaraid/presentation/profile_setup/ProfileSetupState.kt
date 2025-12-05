package uk.ac.tees.mad.scholaraid.presentation.profile_setup

data class ProfileSetupState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,

    // Form fields
    val fullName: String = "",
    val academicLevel: String = "",
    val fieldOfStudy: String = "",
    val gpa: String = "",
    val university: String = "",

    // Form validation
    val fullNameError: String? = null,
    val academicLevelError: String? = null,
    val fieldOfStudyError: String? = null
)