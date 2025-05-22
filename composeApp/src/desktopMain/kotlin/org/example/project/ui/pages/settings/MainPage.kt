package org.example.project.ui.pages.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import org.example.project.data.database.DesktopDatabaseFactory
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
actual fun MainPage() {
    var selectedMenu by remember { mutableStateOf(settingsMenuItems[0].first) }

    val repository = remember {
        DesktopDatabaseFactory().createDatabase()
            .build()
            .userDao()
    }
    Row(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .width(150.dp)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
            settingsMenuItems.forEach { item ->
                val isSelected = item.first == selectedMenu
                val tint = if (isSelected) {
                    MiuixTheme.colorScheme.onSurfaceContainer.copy(alpha = 0.6f)
                } else {
                    MiuixTheme.colorScheme.onSurfaceContainerVariant.copy(alpha = 0.6f)
                }
                Box(
                    Modifier
                        .fillMaxWidth()
                        .background(
                            if (isSelected) MiuixTheme.colorScheme.primary.copy(alpha = 0.1f)
                            else MiuixTheme.colorScheme.surface
                        )
                        .clickable { selectedMenu = item.first }
                        .padding(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            modifier = Modifier
                                .size(32.dp),
                            imageVector = item.second,
                            contentDescription = item.first,
                            colorFilter = ColorFilter.tint(tint)
                        )
                        Text(
                            modifier = Modifier
                                .padding(start = 8.dp),
                            text = item.first
                        )
                    }

                }
            }
        }
        NavPage(repository, PaddingValues())
    }
}
