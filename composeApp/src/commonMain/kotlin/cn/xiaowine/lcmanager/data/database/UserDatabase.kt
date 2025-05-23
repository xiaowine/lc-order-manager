package cn.xiaowine.lcmanager.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import cn.xiaowine.lcmanager.data.dao.UserDao
import cn.xiaowine.lcmanager.data.entity.User

@Database(entities = [User::class], version = 1)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
