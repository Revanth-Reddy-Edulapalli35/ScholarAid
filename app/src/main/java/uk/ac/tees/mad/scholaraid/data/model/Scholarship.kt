package uk.ac.tees.mad.scholaraid.data.model

import uk.ac.tees.mad.scholaraid.data.ScholarshipMapper

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
) {
    val formattedDeadline: String
        get() = ScholarshipMapper.formatDeadline(deadline)

    val isExpired: Boolean
        get() = try {
            val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault())
            val deadlineDate = inputFormat.parse(deadline)
            deadlineDate?.before(java.util.Date()) ?: false
        } catch (e: Exception) {
            false
        }
}