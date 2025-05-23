import kotlinx.serialization.Serializable

@Serializable
data class OrderDetailListData(
    val code: Int,
    val msg: String?,
    val result: OrderDetailListDataResult
)

@Serializable
data class OrderDetailListDataResult(
    val city: String?,
    val grossTotalWeight: Double?,
    val invoiceCompanyName: String?,
    val invoiceHandMoney: Double?,
    val isAllowPay: Boolean,
    val isBookOrder: Boolean,
    val isBrokage: Boolean,
    val isHideCancelButton: Boolean,
    val isOversea: Boolean,
    val isPayArles: Boolean,
    val isStarOrder: Boolean,
    val linkMan: String?,
    val linkPhone: String?,
    val memberDiscountMoney: Double,
    val name: String?,
    val orderCode: String?,
    val orderMoney: Double?,
    val orderRemark: String?,
    val orderStatus: String?,
    val orderTime: String?,
    val orderType: String?,
    val otherMoney: Double?,
    val packageDiscountMoney: Double?,
    val payLimitTime: String?,
    val payStatus: String?,
    val payType: String?,
    val phone: String?,
    val productTotalMoney: Double?,
    val province: String?,
    val purchaseDiscountMoney: Double?,
    val receiveAddress: String?
)

