package org.example.project.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

class AndroidDatabaseFactory(private val context: Context) : DatabaseFactory {
    override fun createDatabase(): RoomDatabase.Builder<UserDatabase> {
        return Room.databaseBuilder(
            context,
            UserDatabase::class.java,
            "user_database"
        )
    }
}
