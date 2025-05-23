package cn.xiaowine.lcmanager.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import cn.xiaowine.lcmanager.data.dao.ProductDao
import cn.xiaowine.lcmanager.data.dao.UserDao
import cn.xiaowine.lcmanager.data.entity.Product
import cn.xiaowine.lcmanager.data.entity.User

@Database(entities = [User::class, Product::class], version = 1)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
}
