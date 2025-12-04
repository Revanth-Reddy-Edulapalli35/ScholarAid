package uk.ac.tees.mad.scholaraid.data.model

data class Scholarship(
    val id: String = "",
    val title: String = "",
    val provider: String = "",
    val amount: String = "",
    val country: String = "",
    val deadline: String = "",
    val applicationLink: String = "",
    val description: String = "",
)