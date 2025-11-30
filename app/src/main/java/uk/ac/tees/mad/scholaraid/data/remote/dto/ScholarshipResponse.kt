package uk.ac.tees.mad.scholaraid.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ScholarshipResponse(
    @SerializedName("data")
    val data: List<ScholarshipDto>,
    @SerializedName("pagination")
    val pagination: PaginationDto?
)

data class ScholarshipDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("provider")
    val provider: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("eligibility")
    val eligibility: String,
    @SerializedName("amount")
    val amount: String,
    @SerializedName("deadline")
    val deadline: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("field")
    val field: String,
    @SerializedName("educationLevel")
    val educationLevel: String,
    @SerializedName("applicationLink")
    val applicationLink: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("updatedAt")
    val updatedAt: String
)

data class PaginationDto(
    @SerializedName("page")
    val page: Int,
    @SerializedName("limit")
    val limit: Int,
    @SerializedName("total")
    val total: Int,
    @SerializedName("pages")
    val pages: Int
)