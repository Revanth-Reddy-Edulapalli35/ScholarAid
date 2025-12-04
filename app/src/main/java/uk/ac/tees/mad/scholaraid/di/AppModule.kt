package uk.ac.tees.mad.scholaraid.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import uk.ac.tees.mad.scholaraid.data.remote.SupabaseConfig
import uk.ac.tees.mad.scholaraid.data.repository.AuthRepositoryImpl
import uk.ac.tees.mad.scholaraid.data.repository.ScholarshipRepositoryImpl
import uk.ac.tees.mad.scholaraid.data.repository.SupabaseImageRepositoryImpl
import uk.ac.tees.mad.scholaraid.data.repository.UserRepositoryImpl
import uk.ac.tees.mad.scholaraid.domain.repository.AuthRepository
import uk.ac.tees.mad.scholaraid.domain.repository.ScholarshipRepository
import uk.ac.tees.mad.scholaraid.domain.repository.SupabaseImageRepository
import uk.ac.tees.mad.scholaraid.domain.repository.UserRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = SupabaseConfig.SUPABASE_URL,
            supabaseKey = SupabaseConfig.SUPABASE_ANON_KEY
        ) {
            // Install the necessary modules
            //install(GoTrue)
            install(Postgrest)
            install(Storage)
        }
    }

    @Provides
    @Singleton
    fun provideAuthRepository(auth: FirebaseAuth): AuthRepository = AuthRepositoryImpl(auth)

    @Provides
    @Singleton
    fun provideScholarshipRepository(
        firestore: FirebaseFirestore
    ): ScholarshipRepository = ScholarshipRepositoryImpl(firestore)

    @Provides
    @Singleton
    fun provideUserRepository(
        firestore: FirebaseFirestore,
        imageRepository: SupabaseImageRepository // <-- FIX: Add this dependency
    ): UserRepository = UserRepositoryImpl(firestore, imageRepository) // <-- FIX: Pass dependency

    @Provides
    @Singleton
    fun provideSupabaseImageRepository(
        client: SupabaseClient // <-- FIX: Add client dependency
    ): SupabaseImageRepository = SupabaseImageRepositoryImpl(client) // <-- FIX: Pass client
}