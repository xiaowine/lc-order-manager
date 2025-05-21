package org.example.project.ui.pages.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.project.data.database.DatabaseFactory
import top.yukonga.miuix.kmp.basic.Text

val settingsMenuItems = listOf("通用", "通知", "隐私", "关于")

@Composable
fun SettingsContent(selectedMenu: String, databaseFactory: DatabaseFactory) {
    when (selectedMenu) {
        "通用" -> SettingsUsersPage(databaseFactory)
        "通知" -> Text("通知设置内容", Modifier.padding(16.dp))
        "隐私" -> Text("隐私设置内容", Modifier.padding(16.dp))
        "关于" -> Text("关于内容", Modifier.padding(16.dp))
        else -> Text("未知", Modifier.padding(16.dp))
    }
}
