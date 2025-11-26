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
    val profileImageUri: String? = null,
    val profileImageBytes: ByteArray? = null,

    // Form validation
    val fullNameError: String? = null,
    val academicLevelError: String? = null,
    val fieldOfStudyError: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProfileSetupState

        if (isLoading != other.isLoading) return false
        if (isSuccess != other.isSuccess) return false
        if (errorMessage != other.errorMessage) return false
        if (fullName != other.fullName) return false
        if (academicLevel != other.academicLevel) return false
        if (fieldOfStudy != other.fieldOfStudy) return false
        if (gpa != other.gpa) return false
        if (university != other.university) return false
        if (profileImageUri != other.profileImageUri) return false
        if (fullNameError != other.fullNameError) return false
        if (academicLevelError != other.academicLevelError) return false
        if (fieldOfStudyError != other.fieldOfStudyError) return false
        if (profileImageBytes != null) {
            if (other.profileImageBytes == null) return false
            if (!profileImageBytes.contentEquals(other.profileImageBytes)) return false
        } else if (other.profileImageBytes != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isLoading.hashCode()
        result = 31 * result + isSuccess.hashCode()
        result = 31 * result + (errorMessage?.hashCode() ?: 0)
        result = 31 * result + fullName.hashCode()
        result = 31 * result + academicLevel.hashCode()
        result = 31 * result + fieldOfStudy.hashCode()
        result = 31 * result + gpa.hashCode()
        result = 31 * result + university.hashCode()
        result = 31 * result + (profileImageUri?.hashCode() ?: 0)
        result = 31 * result + (profileImageBytes?.contentHashCode() ?: 0)
        result = 31 * result + (fullNameError?.hashCode() ?: 0)
        result = 31 * result + (academicLevelError?.hashCode() ?: 0)
        result = 31 * result + (fieldOfStudyError?.hashCode() ?: 0)
        return result
    }
}