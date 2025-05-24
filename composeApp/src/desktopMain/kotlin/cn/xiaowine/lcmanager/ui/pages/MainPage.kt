package cn.xiaowine.lcmanager.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import cn.xiaowine.lcmanager.data.database.DesktopDatabaseFactory
import cn.xiaowine.lcmanager.data.database.UserDatabase
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.VerticalDivider
import top.yukonga.miuix.kmp.theme.MiuixTheme


@Composable
actual fun HomeTopBar(navController: NavHostController) {
}

@Composable
actual fun HomeContent(navController: NavHostController, padding: PaddingValues): Pair<UserDatabase, PaddingValues> {
    val repository = remember {
        DesktopDatabaseFactory().createDatabase()
            .build()
    }
    return repository to PaddingValues(start = (150.75).dp)
}

@Composable
actual fun HomeBottomBar(navController: NavHostController) {
    var selectedMenu by remember { mutableStateOf(settingsMenuItems[0].name) }

    Row {
        Column(
            Modifier
                .width(150.dp)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .background(MiuixTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
            ) {
                Text(
                    text = "立创商城\n订单辅助",
                    color = MiuixTheme.colorScheme.primary,
                    style = MiuixTheme.textStyles.main.copy(fontWeight = FontWeight.Bold)
                )
            }
            settingsMenuItems.forEach { item ->
                val isSelected = item.name == selectedMenu
                val tint = if (isSelected) {
                    MiuixTheme.colorScheme.onSurfaceContainer
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
                        .clickable {
                            selectedMenu = item.name
                            navController.navigate(item.route)
                        }
                        .padding(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            modifier = Modifier
                                .size(32.dp),
                            imageVector = item.icon,
                            contentDescription = item.name,
                            colorFilter = ColorFilter.tint(tint)
                        )
                        Text(
                            modifier = Modifier
                                .padding(start = 8.dp),
                            text = item.name,
                            style = TextStyle(fontSize = 15.sp)
                        )
                    }

                }
            }
        }

        VerticalDivider()
    }
}