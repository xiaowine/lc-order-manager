package cn.xiaowine.lcmanager.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import cn.xiaowine.lcmanager.data.dao.AllProductDao
import cn.xiaowine.lcmanager.data.dao.OrderProductDao
import cn.xiaowine.lcmanager.data.dao.UserDao
import cn.xiaowine.lcmanager.data.entity.AllProduct
import cn.xiaowine.lcmanager.data.entity.OrderProduct
import cn.xiaowine.lcmanager.data.entity.User

@Database(entities = [User::class, OrderProduct::class, AllProduct::class], version = 1)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun productDao(): OrderProductDao
    abstract fun allProductDao(): AllProductDao
}
