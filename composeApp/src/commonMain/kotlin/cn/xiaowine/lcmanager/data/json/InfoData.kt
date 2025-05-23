package cn.xiaowine.lcmanager.data.json

import kotlinx.serialization.Serializable

@Serializable
data class InfoData(
    val code: Int = 0,
    val msg: String?,
    val result: InfoDataResult? = null
)

@Serializable
data class InfoDataResult(
    val addOrder: Boolean?,
    val bindPhone: String?,
    val casAccountCode: String?,
    val casModifyTime: String?,
    val certifyStatus: String?,
    val company: String?,
    val creditIntegral: Int?,
    val customerCode: String?,
    val customerId: Int?,
    val customerIntegral: Int?,
    val customerType: String?,
    val gender: String?,
    val lastLoginTime: String?,
    val nickName: String?,
    val qq: String?,
    val realName: String?,
    val securityCode: String?,
    val taxNumber: String?,
    val uuid: String?
)

