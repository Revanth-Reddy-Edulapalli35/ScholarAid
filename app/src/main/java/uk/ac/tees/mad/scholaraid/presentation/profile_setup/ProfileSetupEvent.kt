package uk.ac.tees.mad.scholaraid.presentation.profile_setup

sealed class ProfileSetupEvent {
    data class FullNameChanged(val fullName: String) : ProfileSetupEvent()
    data class AcademicLevelChanged(val academicLevel: String) : ProfileSetupEvent()
    data class FieldOfStudyChanged(val fieldOfStudy: String) : ProfileSetupEvent()
    data class GpaChanged(val gpa: String) : ProfileSetupEvent()
    data class UniversityChanged(val university: String) : ProfileSetupEvent()
    data class ProfileImageSelected(val imageBytes: ByteArray, val imageUri: String?) : ProfileSetupEvent() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ProfileImageSelected

            if (!imageBytes.contentEquals(other.imageBytes)) return false
            if (imageUri != other.imageUri) return false

            return true
        }

        override fun hashCode(): Int {
            var result = imageBytes.contentHashCode()
            result = 31 * result + (imageUri?.hashCode() ?: 0)
            return result
        }
    }

    object TakePhoto : ProfileSetupEvent()
    object SelectFromGallery : ProfileSetupEvent()
    object SaveProfile : ProfileSetupEvent()
    object ClearError : ProfileSetupEvent()
}