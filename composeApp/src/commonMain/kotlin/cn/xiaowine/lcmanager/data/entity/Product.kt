package cn.xiaowine.lcmanager.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "products")
data class Product(
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
)
