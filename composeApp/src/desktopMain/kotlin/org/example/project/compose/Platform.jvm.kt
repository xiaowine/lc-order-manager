package org.example.project.compose

class JVMPlatform: Platform {
    override val isWindows: Boolean
        get() = true
    override val isAndroid: Boolean
        get() = false

}

actual fun getPlatform(): Platform = JVMPlatform()