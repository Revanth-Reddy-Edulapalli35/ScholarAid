package uk.ac.tees.mad.scholaraid.data.repository

import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import uk.ac.tees.mad.scholaraid.data.model.Scholarship
import uk.ac.tees.mad.scholaraid.domain.repository.ScholarshipRepository
import javax.inject.Inject

class ScholarshipRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore
): ScholarshipRepository {

    override suspend fun getScholarships(): List<Scholarship> {
        return try {
            val snapshot = db.collection("scholarships").get().await()
            // IMPORTANT: Map the document ID (it.id) to the Scholarship model's ID field
            snapshot.documents.mapNotNull {
                it.toObject(Scholarship::class.java)?.copy(id = it.id)
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    override suspend fun getScholarshipById(scholarshipId: String): Scholarship? {
        return try {
            val snapshot = db.collection("scholarships").document(scholarshipId).get().await()
            // Map the document to the Scholarship model and include the ID
            snapshot.toObject(Scholarship::class.java)?.copy(id = scholarshipId)
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun getScholarshipsByIds(ids: List<String>): List<Scholarship> {
        if (ids.isEmpty()) {
            return emptyList()
        }

        return try {
            // Firestore 'whereIn' queries can handle up to 30 values
            // For > 30, you would need to batch the requests
            val snapshot = db.collection("scholarships")
                .whereIn(FieldPath.documentId(), ids)
                .get()
                .await()

            snapshot.documents.mapNotNull {
                it.toObject(Scholarship::class.java)?.copy(id = it.id)
            }
        } catch (_: Exception) {
            emptyList()
        }
    }
}