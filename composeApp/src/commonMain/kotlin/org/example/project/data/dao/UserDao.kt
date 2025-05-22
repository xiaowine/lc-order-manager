package org.example.project.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.example.project.data.entity.User

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("SELECT * FROM users WHERE name = :name LIMIT 1")
    suspend fun findUserByName(name: String): User?

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE name = :name)")
    suspend fun existUserByName(name: String): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE key = :key)")
    suspend fun existUserByKey(key: String): Boolean
}
