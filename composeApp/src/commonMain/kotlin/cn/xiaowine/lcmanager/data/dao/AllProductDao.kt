package cn.xiaowine.lcmanager.data.dao

import androidx.room.*
import cn.xiaowine.lcmanager.data.entity.AllProduct
import cn.xiaowine.lcmanager.data.entity.OrderProduct
import kotlinx.coroutines.flow.Flow

@Dao
interface AllProductDao {
    @Query("SELECT * FROM ${AllProduct.TABLE_NAME}")
    fun getAllProducts(): Flow<List<AllProduct>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(user: AllProduct)

    @Delete
    suspend fun deleteProduct(user: AllProduct)

    @Query("DELETE FROM ${AllProduct.TABLE_NAME}")
    suspend fun deleteAllProducts()
}
