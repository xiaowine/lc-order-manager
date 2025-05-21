package org.example.project.compose

class AndroidPlatform : Platform {
    override val isWindows: Boolean
        get() = false
    override val isAndroid: Boolean
        get() = true
}

actual fun getPlatform(): Platform = AndroidPlatform()