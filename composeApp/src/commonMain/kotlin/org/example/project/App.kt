package org.example.project

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.darkColorScheme
import top.yukonga.miuix.kmp.theme.lightColorScheme
import org.example.project.ui.pages.settings.MainPage

@Composable
@Preview
fun App() {
    val colors = if (isSystemInDarkTheme()) {
        lightColorScheme()
//        darkColorScheme()
    } else {
        lightColorScheme()
    }
    MiuixTheme(
        colors = colors
    ) {
        MainPage()
    }
}