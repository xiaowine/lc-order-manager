package cn.xiaowine.lcmanager.data.entity

// ProductInfo临时类，用于存储产品基本信息
data class ProductInfo(
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
)