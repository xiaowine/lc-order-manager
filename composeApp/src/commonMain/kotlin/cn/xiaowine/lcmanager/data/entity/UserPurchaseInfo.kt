package cn.xiaowine.lcmanager.data.entity

import kotlinx.serialization.Serializable

/**
 * 用户购买信息实体类
 */
@Serializable
data class UserPurchaseInfo(
    // 用户ID
    var userName: String,

    // 购买数量
    var purchaseNumber: Int,

    // 已使用数量
    var usedNumber: Int
)
