package cn.xiaowine.lcmanager

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cn.xiaowine.lcmanager.data.database.UserDatabase
import cn.xiaowine.lcmanager.ui.pages.settings.HomeBottomBar
import cn.xiaowine.lcmanager.ui.pages.settings.HomeContent
import cn.xiaowine.lcmanager.ui.pages.settings.HomeTopBar
import cn.xiaowine.lcmanager.ui.pages.settings.OrdersPage
import cn.xiaowine.lcmanager.ui.pages.settings.SettingsUsersPage
import org.jetbrains.compose.ui.tooling.preview.Preview
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.lightColorScheme

@Composable
@Preview
fun App() {

    val navController = rememberNavController()

    val colors = if (isSystemInDarkTheme()) {
        lightColorScheme()
//        darkColorScheme()
    } else {
        lightColorScheme()
    }
    MiuixTheme(
        colors = colors
    ) {
        Scaffold(
            topBar = {
                HomeTopBar(navController)
            },
            content = { padding ->
                val homeContent = HomeContent(navController, padding)
                NavHost(
                    navController = navController,
                    startDestination = "orders",
                    exitTransition = { fadeOut(targetAlpha = 1f) },
                    enterTransition = { fadeIn(initialAlpha = 1f) },
                ) {
                    pageDestinations(homeContent.first, homeContent.second)
                }
            },
            bottomBar = {
                HomeBottomBar(navController)
            }
        )
    }
}

fun NavGraphBuilder.pageDestinations(
    userDatabase: UserDatabase,
    padding: PaddingValues
) {
    composable("users") {
        SettingsUsersPage(userDatabase.userDao(), padding)
    }
    composable("counts") {
        OrdersPage(userDatabase, padding)
    }
    composable("orders") {
        OrdersPage(userDatabase, padding)
    }
}
