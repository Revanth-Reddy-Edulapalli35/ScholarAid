package uk.ac.tees.mad.scholaraid.presentation.profile_setup

sealed class ProfileSetupEvent {
    data class FullNameChanged(val fullName: String) : ProfileSetupEvent()
    data class AcademicLevelChanged(val academicLevel: String) : ProfileSetupEvent()
    data class FieldOfStudyChanged(val fieldOfStudy: String) : ProfileSetupEvent()
    data class GpaChanged(val gpa: String) : ProfileSetupEvent()
    data class UniversityChanged(val university: String) : ProfileSetupEvent()
    // Removed ProfileImageSelected event
    object SaveProfile : ProfileSetupEvent()
    object ClearError : ProfileSetupEvent()
}