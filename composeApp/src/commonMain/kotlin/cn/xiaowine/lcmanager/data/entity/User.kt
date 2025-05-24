package cn.xiaowine.lcmanager.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = User.TABLE_NAME)
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val key: String
) {
    companion object {
        const val TABLE_NAME = "users"
    }
}
