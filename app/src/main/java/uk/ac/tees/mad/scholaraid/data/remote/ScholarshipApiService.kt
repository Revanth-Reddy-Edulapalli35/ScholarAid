package uk.ac.tees.mad.scholaraid.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import uk.ac.tees.mad.scholaraid.data.remote.dto.ScholarshipResponse

interface ScholarshipApiService {

    @GET("scholarships")
    suspend fun getScholarships(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("country") country: String? = null,
        @Query("educationLevel") educationLevel: String? = null,
        @Query("fieldOfStudy") fieldOfStudy: String? = null
    ): Response<ScholarshipResponse>

    // Note: ScholarshipOwl API might have different parameter names
    // Adjust based on actual API documentation
}