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
import kotlinx.coroutines.launch
import org.example.project.data.UserInputErrorEnum
import org.example.project.data.dao.UserDao
import org.example.project.data.entity.User
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.HorizontalDivider
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.theme.MiuixTheme

data class UserFormState(
    val userName: String = "",
    val key: String = "",
    val isError: Boolean = false,
    val errorType: UserInputErrorEnum = UserInputErrorEnum.NONE
)

@Composable
fun SettingsUsersPage(repository: UserDao, padding: PaddingValues) {
    val users by repository.getAllUsers().collectAsState(initial = emptyList())
    var formState by remember { mutableStateOf(UserFormState()) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp)
    ) {
        UserInputForm(
            formState = formState,
            onFormStateChange = { formState = it },
            onSubmit = { userName, key ->
                coroutineScope.launch {
                    handleUserSubmission(repository, userName, key) { newState ->
                        formState = newState
                    }
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
    formState: UserFormState,
    onFormStateChange: (UserFormState) -> Unit,
    onSubmit: (String, String) -> Unit
) {
    if (formState.isError) {
        Text(formState.errorType.name)
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        TextField(
            value = formState.userName,
            onValueChange = { onFormStateChange(formState.copy(userName = it)) },
            label = "用户名",
            modifier = Modifier.weight(0.4f).padding(end = 8.dp),
            singleLine = true,
        )
        TextField(
            value = formState.key,
            onValueChange = { onFormStateChange(formState.copy(key = it)) },
            label = "Key",
            modifier = Modifier.weight(0.6f).padding(end = 8.dp),
            singleLine = true,
        )
        Button(
            onClick = { onSubmit(formState.userName, formState.key) }
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

    Row(
        Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
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
        Button(
            modifier = Modifier.padding(start = 8.dp),
            onClick = onDelete
        ) {
            Text(
                text = "删除",
                color = MiuixTheme.colorScheme.primary
            )
        }
    }
}

private suspend fun handleUserSubmission(
    repository: UserDao,
    userName: String,
    key: String,
    onStateUpdate: (UserFormState) -> Unit
) {
    when {
        userName.isBlank() -> onStateUpdate(UserFormState(userName = userName, key = key, isError = true, errorType = UserInputErrorEnum.EMPTY_NAME))
        key.isBlank() -> onStateUpdate(UserFormState(userName = userName, key = key, isError = true, errorType = UserInputErrorEnum.EMPTY_KEY))
        repository.existUserByName(userName) -> onStateUpdate(UserFormState(userName = userName, key = key, isError = true, errorType = UserInputErrorEnum.REPEAT_NAME))
        repository.existUserByKey(key) -> onStateUpdate(UserFormState(userName = userName, key = key, isError = true, errorType = UserInputErrorEnum.REPEAT_KEY))
        else -> {
            repository.insertUser(User(name = userName, key = key))
            onStateUpdate(UserFormState())
        }
    }
}
