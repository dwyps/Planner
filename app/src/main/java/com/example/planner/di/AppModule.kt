package com.example.planner.di

import android.content.Context
import com.example.planner.data.local.AppDatabase
import com.example.planner.data.local.TasksDao
import com.example.planner.data.remote.NetworkService
import com.example.planner.data.remote.TasksRemoteDataSource
import com.example.planner.data.repository.Repository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun provideFirebaseDB(): FirebaseDatabase = FirebaseDatabase.getInstance()

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("https://rickandmortyapi.com/api/")
        .build()

    @Singleton
    @Provides
    fun provideNetworkService(retrofit: Retrofit): NetworkService = retrofit.create(NetworkService::class.java)

    @Singleton
    @Provides
    fun provideTodoRemoteDataSource(networkService: NetworkService) = TasksRemoteDataSource(networkService)

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context) = AppDatabase.getDatabase(appContext)

    @Singleton
    @Provides
    fun provideTasksDao(db: AppDatabase) = db.tasksDao()

    @Singleton
    @Provides
    fun provideRepository(
        remoteDataSource: TasksRemoteDataSource,
        localDataSource: TasksDao,
        firebaseAuth: FirebaseAuth,
        firebaseDatabase: FirebaseDatabase
    ) = Repository(remoteDataSource, localDataSource, firebaseAuth, firebaseDatabase)
}