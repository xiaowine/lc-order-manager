package org.example.project.ui.pages.settings

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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.project.data.UserInputErrorEnum
import org.example.project.data.dao.UserDao
import org.example.project.data.entity.User
import org.example.project.data.network.MemberApi.getCustomerInfo
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.HorizontalDivider
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.basic.InfiniteProgressIndicator

private interface FormState {
    var userName: MutableState<String>
    var key: MutableState<String>
    var isError: MutableState<Boolean>
    var errorType: MutableState<UserInputErrorEnum>
}

@Composable
fun SettingsUsersPage(repository: UserDao, padding: PaddingValues) {
    val users by repository.getAllUsers().collectAsState(initial = emptyList())
    val formState = remember {
        object : FormState {
            override var userName = mutableStateOf("")
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
            onSubmit = { userName, key ->
                coroutineScope.launch {
                    handleUserSubmission(
                        repository,
                        userName,
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
    onSubmit: (String, String) -> Unit
) {
    if (formState.isError.value) {
        Text(formState.errorType.value.name)
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        TextField(
            value = formState.userName.value,
            onValueChange = { formState.userName.value = it },
            label = "用户名",
            modifier = Modifier
                .weight(0.3f)
                .padding(end = 8.dp),
            singleLine = true,
        )
        TextField(
            value = formState.key.value,
            onValueChange = { formState.key.value = it },
            label = "Key",
            modifier = Modifier
                .weight(0.6f)
                .padding(end = 8.dp),
            singleLine = true,
        )
        Button(
            onClick = {
                onSubmit(formState.userName.value, formState.key.value)
            }
        ) {
            Text(
                text = "添加",
                color = MiuixTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun UsersList(
    users: List<User>,
    onDeleteUser: (User) -> Unit
) {
    Text(
        modifier = Modifier.padding(top = 16.dp),
        text = "用户列表"
    )
    LazyColumn(
        modifier = Modifier.padding(top = 8.dp)
    ) {
        itemsIndexed(users) { _, user ->
            UserListItem(user = user, onDelete = { onDeleteUser(user) })
            HorizontalDivider()
        }
    }
}

@Composable
private fun UserListItem(
    user: User,
    onDelete: () -> Unit
) {
    var isKeyVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var verificationResult by remember { mutableStateOf<Boolean?>(null) }
    val coroutineScope = rememberCoroutineScope()

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
                if (isKeyVisible) user.key else "点击显示Key",
                color = Color.Gray,
                modifier = Modifier
                    .clickable { isKeyVisible = !isKeyVisible }
                    .padding(top = 4.dp),
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isLoading) {
                InfiniteProgressIndicator(
                    modifier = Modifier.padding(8.dp),
                    color = MiuixTheme.colorScheme.primary
                )
            }
            verificationResult?.let { result ->
                Text(
                    text = "验证\n${if (result) "成功" else "失败"}",
                    color = if (result) Color(74, 109, 66) else Color(168, 62, 76),
                    modifier = Modifier.padding(top = 4.dp).clickable {
                        verificationResult = null
                    }
                )
                LaunchedEffect(Unit) {
                    if (verificationResult == false) return@LaunchedEffect
                    delay(10000)
                    verificationResult = null
                }
            }
            Button(
                onClick = {
                    isLoading = true
                    coroutineScope.launch {
                        try {
                            val response = getCustomerInfo(user.key)
                            verificationResult = response.code == 200
                        } finally {
                            isLoading = false
                        }
                    }
                }
            ) {
                Text(
                    text = "验证",
                    color = MiuixTheme.colorScheme.primary
                )
            }

            Button(
                onClick = onDelete
            ) {
                Text(
                    text = "删除",
                    color = MiuixTheme.colorScheme.primary
                )
            }
        }
    }
}

private suspend fun handleUserSubmission(
    repository: UserDao,
    userName: String,
    key: String,
    formState: FormState,
    onError: () -> Unit
) {
    when {
        userName.isBlank() -> {
            formState.apply {
                this.userName.value = userName
                this.key.value = key
                isError.value = true
                errorType.value = UserInputErrorEnum.EMPTY_NAME
            }
        }

        key.isBlank() -> {
            formState.apply {
                this.userName.value = userName
                this.key.value = key
                isError.value = true
                errorType.value = UserInputErrorEnum.EMPTY_KEY
            }
        }

        repository.existUserByName(userName) -> {
            formState.apply {
                this.userName.value = userName
                this.key.value = key
                isError.value = true
                errorType.value = UserInputErrorEnum.REPEAT_NAME
            }
        }

        repository.existUserByKey(key) -> {
            formState.apply {
                this.userName.value = userName
                this.key.value = key
                isError.value = true
                errorType.value = UserInputErrorEnum.REPEAT_KEY
            }
        }

        else -> {
            val user = User(name = userName, key = key)
            val response = getCustomerInfo(key)
            if (response.code == 200) {
                repository.insertUser(user)
                formState.apply {
                    this.userName.value = ""
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

