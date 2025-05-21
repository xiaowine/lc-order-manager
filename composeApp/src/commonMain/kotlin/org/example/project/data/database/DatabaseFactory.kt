package org.example.project.data.database

import androidx.room.RoomDatabase

interface DatabaseFactory {
    fun createDatabase(): RoomDatabase.Builder<UserDatabase>
}
