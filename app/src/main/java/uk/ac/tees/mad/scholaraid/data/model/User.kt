package uk.ac.tees.mad.scholaraid.data.model

data class User(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val profilePhotoUrl: String = "",
    val educationLevel: String = "", // High school, College, Researcher
    val fieldOfStudy: String = "",
    val gpa: String = "",
    val createdAt: Long = System.currentTimeMillis()
)