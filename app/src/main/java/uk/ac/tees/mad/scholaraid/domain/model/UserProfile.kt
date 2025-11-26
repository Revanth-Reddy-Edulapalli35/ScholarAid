package uk.ac.tees.mad.scholaraid.domain.model

data class UserProfile(
    val userId: String = "",
    val email: String = "",
    val fullName: String = "",
    val profilePhotoUrl: String = "",
    val academicLevel: String = "",
    val fieldOfStudy: String = "",
    val gpa: String = "",
    val university: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        val ACADEMIC_LEVELS = listOf(
            "High School",
            "Undergraduate",
            "Graduate",
            "Postgraduate",
            "Researcher"
        )

        val FIELDS_OF_STUDY = listOf(
            "Computer Science",
            "Engineering",
            "Business",
            "Medicine",
            "Arts",
            "Sciences",
            "Law",
            "Education",
            "Other"
        )
    }
}