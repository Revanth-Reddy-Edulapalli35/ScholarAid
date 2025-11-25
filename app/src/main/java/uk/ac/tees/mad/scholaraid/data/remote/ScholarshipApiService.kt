package uk.ac.tees.mad.scholaraid.data.remote

import com.google.android.gms.common.api.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import uk.ac.tees.mad.scholaraid.data.remote.dto.ScholarshipDto
import uk.ac.tees.mad.scholaraid.data.remote.dto.ScholarshipResponse

interface ScholarshipApiService {

//    @GET("scholarships")
//    suspend fun getScholarships(
//        @Query("level") level: String? = null,
//        @Query("field") field: String? = null,
//        @Query("country") country: String? = null
//    ): Response<ScholarshipResponse>
//
//    @GET("scholarships/{id}")
//    suspend fun getScholarshipById(
//        @Path("id") id: String
//    ): Response<ScholarshipDto>
}