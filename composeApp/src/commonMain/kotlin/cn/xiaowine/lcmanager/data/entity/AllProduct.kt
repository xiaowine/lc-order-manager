package cn.xiaowine.lcmanager.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import cn.xiaowine.lcmanager.data.converter.Converters


@Entity(tableName = AllProduct.TABLE_NAME)
@TypeConverters(Converters::class)
data class AllProduct(
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
    val stockUnit: String,
    val uuid: String,
    val orderCode: String,
    val orderUuid: String,
    val orderTime: String,

    // 存储每个用户的购买和使用情况
    val userPurchaseInfos: List<UserPurchaseInfo> = emptyList()
) {

    // 获取总购买数量
    val totalPurchaseNumber: Int
        get() = userPurchaseInfos.sumOf { it.purchaseNumber }

    // 获取总使用数量
    val totalUsedNumber: Int
        get() = userPurchaseInfos.sumOf { it.usedNumber }

    companion object {
        const val TABLE_NAME = "all_product"
    }
}
