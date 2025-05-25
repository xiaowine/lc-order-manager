package cn.xiaowine.lcmanager.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import cn.xiaowine.lcmanager.data.database.AndroidDatabaseFactory
import cn.xiaowine.lcmanager.data.database.UserDatabase
import top.yukonga.miuix.kmp.basic.HorizontalDivider
import top.yukonga.miuix.kmp.basic.NavigationBar
import top.yukonga.miuix.kmp.basic.NavigationItem
import top.yukonga.miuix.kmp.basic.SmallTopAppBar

@Composable
actual fun HomeTopBar(navController: NavHostController) {
    Column {
        SmallTopAppBar(
            title = "标题",
        )
        HorizontalDivider()
    }

}

@Composable
actual fun HomeContent(navController: NavHostController, padding: PaddingValues): Pair<UserDatabase, PaddingValues> {
    val context = LocalContext.current
    val repository = remember {
        AndroidDatabaseFactory(context).createDatabase()
            .build()
    }
    return repository to padding
}

@Composable
actual fun HomeBottomBar(navController: NavHostController) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    navController.addOnDestinationChangedListener { _, destination, _ ->
        selectedIndex = settingsMenuItems.indexOfFirst { it.route == destination.route }
    }

    NavigationBar(
        items = settingsMenuItems.map {
            NavigationItem(it.name, it.icon)
        },
        selected = selectedIndex,
        onClick = {
            selectedIndex = it
            navController.navigate(settingsMenuItems[selectedIndex].route)
        },
    )
}