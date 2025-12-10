package uk.ac.tees.mad.scholaraid.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uk.ac.tees.mad.scholaraid.data.local.ScholarshipDatabase
import uk.ac.tees.mad.scholaraid.data.local.dao.ScholarshipDao
import uk.ac.tees.mad.scholaraid.data.repository.AuthRepositoryImpl
import uk.ac.tees.mad.scholaraid.data.repository.LocalScholarshipRepositoryImpl
import uk.ac.tees.mad.scholaraid.data.repository.ScholarshipRepositoryImpl
import uk.ac.tees.mad.scholaraid.data.repository.UserRepositoryImpl
import uk.ac.tees.mad.scholaraid.domain.repository.AuthRepository
import uk.ac.tees.mad.scholaraid.domain.repository.LocalScholarshipRepository
import uk.ac.tees.mad.scholaraid.domain.repository.ScholarshipRepository
import uk.ac.tees.mad.scholaraid.domain.repository.UserRepository
import uk.ac.tees.mad.scholaraid.presentation.save.SaveViewModel
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
    ): UserRepository = UserRepositoryImpl(firestore)

    @Provides
    @Singleton
    fun provideScholarshipDatabase(@ApplicationContext context: Context): ScholarshipDatabase {
        return ScholarshipDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideScholarshipDao(database: ScholarshipDatabase): ScholarshipDao {
        return database.scholarshipDao()
    }

    @Provides
    @Singleton
    fun provideLocalScholarshipRepository(dao: ScholarshipDao): LocalScholarshipRepository {
        return LocalScholarshipRepositoryImpl(dao)
    }

    // Update SaveViewModel to include LocalScholarshipRepository
    @Provides
    @Singleton
    fun provideSaveViewModel(
        userRepository: UserRepository,
        scholarshipRepository: ScholarshipRepository,
        localScholarshipRepository: LocalScholarshipRepository,
        firebaseAuth: FirebaseAuth
    ): SaveViewModel {
        return SaveViewModel(userRepository, scholarshipRepository, localScholarshipRepository, firebaseAuth)
    }
}