package cn.xiaowine.lcmanager.data.dao

import androidx.room.*
import cn.xiaowine.lcmanager.data.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM ${User.TABLE_NAME}")
    fun getAllUsers(): Flow<List<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("SELECT * FROM ${User.TABLE_NAME} WHERE name = :name LIMIT 1")
    suspend fun findUserByName(name: String): User?

    @Query("SELECT EXISTS(SELECT 1 FROM ${User.TABLE_NAME} WHERE name = :name)")
    suspend fun existUserByName(name: String): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM ${User.TABLE_NAME} WHERE `key` = :userKey)")
    suspend fun existUserByKey(userKey: String): Boolean
}
