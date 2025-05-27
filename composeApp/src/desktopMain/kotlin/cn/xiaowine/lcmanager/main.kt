package cn.xiaowine.lcmanager

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import java.awt.Dimension

fun main() = application {
    val state = rememberWindowState(
        size = DpSize(1000.dp, 800.dp)
    )
    Window(
        onCloseRequest = ::exitApplication,
        title = "LcManager",
        state = state,
    ) {
        window.minimumSize = Dimension(500, 400)
        App()
    }
}

