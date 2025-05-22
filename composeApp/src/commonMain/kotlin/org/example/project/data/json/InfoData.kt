package org.example.project.data.json

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class InfoData(
    val code: Int = 0,
    val msg: String? = null,
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

