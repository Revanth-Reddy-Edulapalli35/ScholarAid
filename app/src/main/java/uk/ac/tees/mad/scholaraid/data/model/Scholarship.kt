package uk.ac.tees.mad.scholaraid.data.model

data class Scholarship(
    val id: String = "",
    val title: String = "",
    val provider: String = "",
    val description: String = "",
    val eligibility: String = "",
    val amount: String = "",
    val deadline: String = "",
    val country: String = "",
    val field: String = "",
    val educationLevel: String = "",
    val applicationLink: String = "",
    val isSaved: Boolean = false
)