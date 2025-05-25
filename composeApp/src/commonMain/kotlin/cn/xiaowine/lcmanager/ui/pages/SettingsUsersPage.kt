package cn.xiaowine.lcmanager.ui.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.xiaowine.lcmanager.data.UserInputErrorEnum
import cn.xiaowine.lcmanager.data.dao.UserDao
import cn.xiaowine.lcmanager.data.entity.User
import cn.xiaowine.lcmanager.data.network.MemberApi.getCustomerInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.HorizontalDivider
import top.yukonga.miuix.kmp.basic.InfiniteProgressIndicator
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme

private interface FormState {
    var key: MutableState<String>
    var isError: MutableState<Boolean>
    var errorType: MutableState<UserInputErrorEnum>
}

@Composable
fun SettingsUsersPage(repository: UserDao, padding: PaddingValues) {
    val users by repository.getAllUsers().collectAsState(initial = emptyList())
    val formState = remember {
        object : FormState {
            override var key = mutableStateOf("")
            override var isError = mutableStateOf(false)
            override var errorType = mutableStateOf(UserInputErrorEnum.NONE)
        }
    }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp)
    ) {
        UserInputForm(
            formState = formState,
            onSubmit = { key ->
                coroutineScope.launch {
                    handleUserSubmission(
                        repository,
                        key,
                        formState = formState,
                        onError = {
                            formState.isError.value = true
                            formState.errorType.value = UserInputErrorEnum.NETWORK_ERROR
                        }
                    )
                }
            }
        )
        UsersList(users = users, onDeleteUser = { user ->
            coroutineScope.launch {
                repository.deleteUser(user)
            }
        })
    }
}

@Composable
private fun UserInputForm(
    formState: FormState,
    onSubmit: (String) -> Unit
) {
    if (formState.isError.value) {
        Text(formState.errorType.value.name)
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        TextField(
            value = formState.key.value,
            onValueChange = { formState.key.value = it },
            label = "请输入立创商城Key",
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            singleLine = true,
        )
        TextButton(
            text = "添加",
            onClick = {
                onSubmit(formState.key.value)
            },
            colors = ButtonDefaults.textButtonColorsPrimary()
        )
    }
}

@Composable
private fun UsersList(
    users: List<User>,
    onDeleteUser: (User) -> Unit
) {
    Text(
        modifier = Modifier.padding(top = 16.dp),
        text = "用户列表（共${users.size}个账号）"
    )
    LazyColumn(
        modifier = Modifier.padding(top = 8.dp)
    ) {
        itemsIndexed(users) { _, user ->
            UserListItem(user = user, onDelete = { onDeleteUser(user) })
        }
    }
}

@Composable
private fun UserListItem(
    user: User,
    onDelete: () -> Unit
) {
    var isKeyVisible = remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var verificationResult by remember { mutableStateOf<Boolean?>(null) }
    val coroutineScope = rememberCoroutineScope()

    SuperDialog(
        modifier = Modifier,
        show = isKeyVisible,
        onDismissRequest = { isKeyVisible.value = false },
        title = "请勿泄露本KEY",
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SelectionContainer {
                Text(user.key)
            }
        }
        TextButton(
            text = "确定",
            onClick = { isKeyVisible.value = false },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = ButtonDefaults.textButtonColorsPrimary()
        )
    }
    Row(
        Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(user.name)
            Text(
                text = "点击显示Key",
                color = Color.Gray,
                maxLines = 1,
                modifier = Modifier
                    .clickable {
                        isKeyVisible.value = true
                    }
                    .padding(top = 4.dp),
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            AnimatedVisibility(
                visible = isLoading,
            ) {
                InfiniteProgressIndicator(
                    modifier = Modifier.padding(8.dp),
                    color = MiuixTheme.colorScheme.primary
                )
            }
            AnimatedVisibility(
                visible = verificationResult != null
            ) {
                verificationResult?.let {
                    Text(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .clickable {
                                verificationResult = null
                            },
                        text = if (it) "有效" else "失效",
                        color = if (it) Color(74, 109, 66) else Color(168, 62, 76),
                    )
                    LaunchedEffect(Unit) {
                        if (!it) return@LaunchedEffect
                        delay(10000)
                        verificationResult = null
                    }
                }
            }
            TextButton(
                text = "验证",
                onClick = {
                    isLoading = true
                    verificationResult = null
                    coroutineScope.launch {
                        val response = getCustomerInfo(user.key)
                        verificationResult = response.code == 200
                        isLoading = false
                    }
                }
            )

            TextButton(
                text = "删除",
                onClick = onDelete
            )
        }
    }

    HorizontalDivider()
}

private suspend fun handleUserSubmission(
    repository: UserDao,
    key: String,
    formState: FormState,
    onError: () -> Unit
) {
    when {
        key.isBlank() -> {
            formState.apply {
                this.key.value = key
                isError.value = true
                errorType.value = UserInputErrorEnum.EMPTY_KEY
            }
        }

        repository.existUserByKey(key) -> {
            formState.apply {
                this.key.value = key
                isError.value = true
                errorType.value = UserInputErrorEnum.REPEAT_KEY
            }
        }

        else -> {
            val response = getCustomerInfo(key)
            if (response.code == 200) {
                val nickName = response.result?.nickName ?: "未知用户"
                if (repository.existUserByName(nickName)) {
                    formState.apply {
                        this.key.value = key
                        isError.value = true
                        errorType.value = UserInputErrorEnum.REPEAT_NAME
                    }
                    return
                }
                val user = User(name = nickName, key = key)
                repository.insertUser(user)
                formState.apply {
                    this.key.value = ""
                    isError.value = false
                    errorType.value = UserInputErrorEnum.NONE
                }
            } else {
                onError()
            }
        }
    }
}

