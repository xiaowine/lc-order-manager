package cn.xiaowine.lcmanager.data.converter

import androidx.room.TypeConverter
import cn.xiaowine.lcmanager.data.entity.UserPurchaseInfo
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromUserPurchaseInfoList(value: List<UserPurchaseInfo>?): String? {
        return value?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toUserPurchaseInfoList(value: String?): List<UserPurchaseInfo>? {
        return value?.let { Json.decodeFromString(it) }
    }
}

