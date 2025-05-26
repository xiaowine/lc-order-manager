package cn.xiaowine.lcmanager.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.extra.SuperDialog


@Composable
fun ErrorDialog(
    isNetworkErrorDialogVisible: MutableState<Boolean>
) {
    SuperDialog(
        modifier = Modifier,
        show = isNetworkErrorDialogVisible,
        title = "网络错误",
        onDismissRequest = { isNetworkErrorDialogVisible.value = false }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "获取失败，请检查网络稍后再试",
                color = Color.Red
            )

            TextButton(
                text = "确定",
                onClick = { isNetworkErrorDialogVisible.value = false },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                colors = ButtonDefaults.textButtonColorsPrimary()
            )
        }
    }
}