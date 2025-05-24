package cn.xiaowine.lcmanager.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = OrderProduct.TABLE_NAME)
data class OrderProduct(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val brandName: String,
    val productCode: String,
    val breviaryImageUrl: String,
    val catalogName: String,
    val encapStandard: String,
    val productModel: String,
    val productName: String,
    val productPrice: Double,
    val purchaseNumber: Int,
    val stockUnit: String,
    val uuid: String,
    val orderCode: String,
    val orderUuid: String,
    val orderTime: String,
) {
    companion object {
        const val TABLE_NAME = "order_product"
    }
}
