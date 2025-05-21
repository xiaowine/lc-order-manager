package org.example.project.compose

interface Platform {
    val isWindows: Boolean
    val isAndroid: Boolean
}

expect fun getPlatform(): Platform