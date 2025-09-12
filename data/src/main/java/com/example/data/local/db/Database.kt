package com.example.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.db.dao.NoteDao
import com.example.data.local.db.entities.NoteEntity
import com.example.data.local.secure.DatabasePasswordManager
import net.sqlcipher.database.SupportFactory

@Database(entities = [NoteEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        private const val DB_NAME = "secure_notes_db"

        fun getDatabase(
            context: Context,
            passwordManager: DatabasePasswordManager
        ): AppDatabase {
            val passphrase = passwordManager.getDatabasePassword()
            val factory = SupportFactory(passphrase)

            return Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                .openHelperFactory(factory)
                .build()
        }
    }
}