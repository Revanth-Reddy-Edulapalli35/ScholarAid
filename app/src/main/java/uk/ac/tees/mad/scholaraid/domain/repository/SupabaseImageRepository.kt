package uk.ac.tees.mad.scholaraid.domain.repository

import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.scholaraid.util.Resource

interface SupabaseImageRepository {
    /**
     * Uploads an image as a ByteArray and returns a Flow of the upload status and the final URL.
     * @param userId The ID of the user (e.g., Firebase Auth UID) to use in the file path.
     * @param imageBytes The ByteArray of the image to upload.
     * @return A Flow emitting Resource states (Loading, Success<String> with URL, Error).
     */
    fun uploadProfileImage(userId: String, imageBytes: ByteArray): Flow<Resource<String>>
}