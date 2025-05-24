package cn.xiaowine.lcmanager.ui.pages

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import cn.xiaowine.lcmanager.data.database.UserDatabase
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.useful.NavigatorSwitch

data class PageData(
    val name: String,
    val icon: ImageVector,
    val route: String,
)

val settingsMenuItems = listOf(
    PageData("订单", MiuixIcons.Useful.NavigatorSwitch, "orders"),
    PageData("统计", MiuixIcons.Useful.NavigatorSwitch, "counts"),
    PageData("用户", MiuixIcons.Useful.NavigatorSwitch, "users"),
)

@Composable
expect fun HomeTopBar(navController: NavHostController)

@Composable
expect fun HomeContent(navController: NavHostController, padding: PaddingValues): Pair<UserDatabase, PaddingValues>

@Composable
expect fun HomeBottomBar(navController: NavHostController)

