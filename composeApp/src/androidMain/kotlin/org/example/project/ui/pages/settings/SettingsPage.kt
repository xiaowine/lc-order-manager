package org.example.project.ui.pages.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import org.example.project.data.database.AndroidDatabaseFactory
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
actual fun SettingsPage() {
    var selectedMenu by remember { mutableStateOf(settingsMenuItems[0]) }
    var showContent by remember { mutableStateOf(false) }
    var tempSelected by remember { mutableStateOf(selectedMenu) }
    val context = LocalContext.current
    if (!showContent) {
        Column(
            Modifier
                .fillMaxSize()
                .background(MiuixTheme.colorScheme.surface)
                .verticalScroll(rememberScrollState())
        ) {
            settingsMenuItems.forEach { item ->
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            tempSelected = item
                            showContent = true
                        }
                        .padding(16.dp)
                ) {
                    Text(item)
                }
            }
        }
    } else {
        Box(
            Modifier
                .fillMaxSize()
                .background(MiuixTheme.colorScheme.background)
        ) {
            Column {
                Text(
                    "< 返回",
                    Modifier
                        .clickable {
                            showContent = false
                            selectedMenu = tempSelected
                        }
                        .padding(16.dp)
                )
                SettingsContent(tempSelected, AndroidDatabaseFactory(context))
            }
        }
    }
}
