package cn.xiaowine.lcmanager.data.json

import kotlinx.serialization.Serializable


@Serializable
data class OrderListData(
    val code: Int,
    val msg: String?,
    val result: OrderListDataResult? = null
)

@Serializable
data class OrderListDataResult(
    val currPage: Int,
    val dataList: List<Data>,
    val pageRow: Int,
    val totalPage: Int,
    val totalRow: Int
)

@Serializable
data class Data(
    val uuid: String,
    val activityType: String?,
    val invoiceCompanyName: String?,
    val isAllowPay: Boolean,
    val isBookOrder: Boolean,
    val isHideCancelButton: Boolean,
    val isOversea: Boolean,
    val isPayArles: Boolean,
    val orderCode: String,
    val orderTime: String,
    val phoneOrderDetailVOList: List<PhoneOrderDetailVO>
)

@Serializable
data class PhoneOrderDetailVO(
    val brandName: String?,
    val breviaryImageUrl: String?,
    val catalogName: String?,
    val encapStandard: String?,
    val isMroProduct: Boolean,
    val productCode: String?,
    val productModel: String?,
    val productName: String?,
    val productPrice: Double?,
    val productSource: String?,
    val productTotalMoney: Double?,
    val productWeight: Double?,
    val purchaseNumber: Int?,
    val stockUnit: String?
)
