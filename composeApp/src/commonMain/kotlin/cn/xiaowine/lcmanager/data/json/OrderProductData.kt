package cn.xiaowine.lcmanager.data.json

import kotlinx.serialization.Serializable

@Serializable
data class OrderProductData(
    val code: Int?,
    val msg: String?,
    val result: OrderProductResult?
)

@Serializable
data class OrderProductResult(
    val szProductList: List<SzProduct>,
    val jsProductList: List<SzProduct>
)

@Serializable
data class SzProduct(
    val activityName: String?,
    val brandName: String?,
    val breviaryImageUrl: String?,
    val catalogName: String?,
    val encapStandard: String?,
    val productArrange: String?,
    val productCode: String?,
    val productId: Int?,
    val productMinEncapsulationNumber: Int?,
    val productModel: String?,
    val productName: String?,
    val productPrice: Double?,
    val productSaleType: Int?,
    val productSignId: String?,
    val productSource: String?,
    val productTotalMoney: Double?,
    val productWeight: Double?,
    val purchaseNumber: Int,
    val stockUnit: String?,
    val uuid: String?,
    val wareHouseCode: String?
)