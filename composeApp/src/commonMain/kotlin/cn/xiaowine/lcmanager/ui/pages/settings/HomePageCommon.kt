package cn.xiaowine.lcmanager.ui.pages.settings

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cn.xiaowine.lcmanager.data.dao.UserDao
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.useful.NavigatorSwitch

val settingsMenuItems = listOf(
    "通知" to MiuixIcons.Useful.NavigatorSwitch,
    "隐私" to MiuixIcons.Useful.NavigatorSwitch,
    "用户管理" to MiuixIcons.Useful.NavigatorSwitch,
)

@Composable
fun NavPage(repository: UserDao, padding: PaddingValues) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "users_settings",
        exitTransition = { fadeOut(targetAlpha = 1f) },
        enterTransition = { fadeIn(initialAlpha = 1f) },
    ) {
        pageDestinations(repository, padding)
    }
}

// 修改 pageDestinations，传递 isSearchState
fun NavGraphBuilder.pageDestinations(
    repository: UserDao,
    padding: PaddingValues
) {
    composable("users_settings") {
        SettingsUsersPage(repository, padding)
    }
}
