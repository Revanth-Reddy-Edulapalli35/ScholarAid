package uk.ac.tees.mad.scholaraid.data.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import uk.ac.tees.mad.scholaraid.data.remote.SupabaseConfig
import uk.ac.tees.mad.scholaraid.domain.repository.SupabaseImageRepository
import uk.ac.tees.mad.scholaraid.util.Resource
import javax.inject.Inject

class SupabaseImageRepositoryImpl @Inject constructor(
    private val client: SupabaseClient // <-- FIX: Inject the client
) : SupabaseImageRepository {

    override fun uploadProfileImage(userId: String, imageBytes: ByteArray): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            // **FIX: Real Supabase Implementation**

            // 1. Define a unique file path.
            // Using the userId as the file name ensures that uploading a new photo
            // will simply overwrite the old one.
            val imagePath = "profile_images/$userId.jpg"

            // 2. Upload 'imageBytes' to Supabase Storage bucket.
            // 'upsert = true' will create or replace the file.
            client.storage
                .from(SupabaseConfig.STORAGE_BUCKET)
                .upload(
                    path = imagePath,
                    data = imageBytes,
                    upsert = true
                )

            // 3. Get the public URL for the uploaded file.
            val publicUrl = client.storage
                .from(SupabaseConfig.STORAGE_BUCKET)
                .publicUrl(path = imagePath)

            // 4. Emit the success with the public URL
            emit(Resource.Success(publicUrl))

        } catch (e: Exception) {
            e.printStackTrace() // Helpful for debugging
            emit(Resource.Error(e.localizedMessage ?: "Failed to upload image to Supabase"))
        }
    }
}