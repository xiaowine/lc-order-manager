package cn.xiaowine.lcmanager.data.dao

import androidx.room.*
import cn.xiaowine.lcmanager.data.entity.OrderProduct
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderProductDao {
    @Query("SELECT * FROM ${OrderProduct.TABLE_NAME}")
    fun getAllProducts(): Flow<List<OrderProduct>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(user: OrderProduct)

    @Delete
    suspend fun deleteProduct(user: OrderProduct)

    @Query("DELETE FROM ${OrderProduct.TABLE_NAME}")
    suspend fun deleteAllProducts()
}
