package org.example.project.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import org.example.project.data.dao.UserDao
import org.example.project.data.entity.User

@Database(entities = [User::class], version = 1)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
