package uk.ac.tees.mad.scholaraid.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uk.ac.tees.mad.scholaraid.data.repository.AuthRepositoryImpl
import uk.ac.tees.mad.scholaraid.data.repository.ScholarshipRepositoryImpl
import uk.ac.tees.mad.scholaraid.data.repository.UserRepositoryImpl
import uk.ac.tees.mad.scholaraid.domain.repository.AuthRepository
import uk.ac.tees.mad.scholaraid.domain.repository.ScholarshipRepository
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
    fun provideAuthRepository(auth: FirebaseAuth): AuthRepository = AuthRepositoryImpl(auth)

    @Provides
    @Singleton
    fun provideScholarshipRepository(
        firestore: FirebaseFirestore
    ): ScholarshipRepository = ScholarshipRepositoryImpl(firestore)

    @Provides
    @Singleton
    fun provideUserRepository(
        firestore: FirebaseFirestore
    ): UserRepository = UserRepositoryImpl(firestore) // Removed imageRepository dependency
}