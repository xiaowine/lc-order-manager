package cn.xiaowine.lcmanager.data.dao

import androidx.room.*
import cn.xiaowine.lcmanager.data.entity.Product
import cn.xiaowine.lcmanager.data.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<Product>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(user: Product)

    @Delete
    suspend fun deleteProduct(user: Product)

    @Query("DELETE FROM products")
    suspend fun deleteAllProducts()
}
