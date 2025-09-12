package com.example.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.note.Note

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis()
)

fun NoteEntity.toDomain() = Note(id, title, content, createdAt)
fun Note.toEntity() = NoteEntity(id, title, content, createdAt)