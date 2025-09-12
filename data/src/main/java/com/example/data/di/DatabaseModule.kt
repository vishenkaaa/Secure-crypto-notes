package com.example.data.di

import android.content.Context
import com.example.data.local.db.AppDatabase
import com.example.data.local.db.dao.NoteDao
import com.example.data.local.secure.DatabasePasswordManager
import com.example.data.local.secure.SecureStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideSecureStorage(
        @ApplicationContext context: Context
    ): SecureStorage = SecureStorage(context)

    @Provides
    @Singleton
    fun provideDatabasePasswordManager(
        secureStorage: SecureStorage
    ): DatabasePasswordManager = DatabasePasswordManager(secureStorage)

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        passwordManager: DatabasePasswordManager
    ): AppDatabase = AppDatabase.getDatabase(context, passwordManager)

    @Provides
    fun provideNoteDao(db: AppDatabase): NoteDao = db.noteDao()
}
