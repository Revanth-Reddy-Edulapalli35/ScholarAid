package uk.ac.tees.mad.scholaraid.data.remote.dto

data class ScholarshipResponse(
    val results: List<ScholarshipDto>
)

data class ScholarshipDto(
    val id: String,
    val title: String,
    val provider: String,
    val description: String,
    val eligibility: String,
    val amount: String,
    val deadline: String,
    val country: String,
    val field: String,
    val level: String,
    val url: String
)