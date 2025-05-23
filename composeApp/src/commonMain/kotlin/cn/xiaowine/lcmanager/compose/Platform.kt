package cn.xiaowine.lcmanager.compose

interface Platform {
    val isWindows: Boolean
    val isAndroid: Boolean
}

expect fun getPlatform(): Platform