package org.example.project.ui.pages.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.project.data.database.DesktopDatabaseFactory
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
actual fun SettingsPage() {
    var selectedMenu by remember { mutableStateOf(settingsMenuItems[0]) }

    Row(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .width(200.dp)
                .fillMaxHeight()
                .background(color = MiuixTheme.colorScheme.surface)
                .verticalScroll(rememberScrollState())
        ) {
            settingsMenuItems.forEach { item ->
                Box(
                    Modifier
                        .fillMaxWidth()
                        .background(
                            if (item == selectedMenu) MiuixTheme.colorScheme.primary.copy(alpha = 0.1f)
                            else MiuixTheme.colorScheme.surface
                        )
                        .clickable { selectedMenu = item }
                        .padding(16.dp)
                ) {
                    Text(item)
                }
            }
        }
        Box(
            Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(MiuixTheme.colorScheme.background)
        ) {
            SettingsContent(selectedMenu, DesktopDatabaseFactory())
        }
    }
}
