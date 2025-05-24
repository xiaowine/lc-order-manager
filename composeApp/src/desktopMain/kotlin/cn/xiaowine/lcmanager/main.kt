package cn.xiaowine.lcmanager

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

fun main() = application {
    val state = rememberWindowState(
        size = DpSize(800.dp, 600.dp)
    )
    Window(
        onCloseRequest = ::exitApplication,
        title = "LcManager",
        state = state,
    ) {
        App()
    }
}

