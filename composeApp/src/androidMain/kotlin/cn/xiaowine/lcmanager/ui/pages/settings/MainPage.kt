package cn.xiaowine.lcmanager.ui.pages.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import cn.xiaowine.lcmanager.data.database.AndroidDatabaseFactory
import top.yukonga.miuix.kmp.basic.NavigationBar
import top.yukonga.miuix.kmp.basic.NavigationItem
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTopAppBar

@Composable
actual fun MainPage() {

    val context = LocalContext.current

    var selectedIndex by remember { mutableIntStateOf(0) }

    val repository = remember {
        AndroidDatabaseFactory(context).createDatabase()
            .build()
            .userDao()
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = "标题",
            )
        },
        content = { padding ->
            NavPage(repository, padding)
        },
        bottomBar = {
            NavigationBar(
                items = settingsMenuItems.map {
                    NavigationItem(it.first, it.second)
                },
                selected = selectedIndex,
                onClick = { selectedIndex = it }
            )
        }
    )
}