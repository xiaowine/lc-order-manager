package org.example.project.ui.pages.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import org.example.project.data.database.DatabaseFactory
import org.example.project.data.entity.User
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField

@Composable
fun SettingsUsersPage(databaseFactory: DatabaseFactory) {
    val repository = remember {
        databaseFactory.createDatabase()
            .build()
            .userDao()
    }
    val users by repository.getAllUsers().collectAsState(initial = emptyList())
    var nameInput by remember { mutableStateOf("") }
    var keyInput by remember { mutableStateOf("") }
    var keyVisibleIndex by remember { mutableStateOf<Int?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = nameInput,
                onValueChange = { nameInput = it },
                label = "用户名",
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            )
            TextField(
                value = keyInput,
                onValueChange = { keyInput = it },
                label = "Key",
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            )
            Button(
                onClick = {
                    coroutineScope.launch {
                        if (nameInput.isNotBlank() && keyInput.isNotBlank()) {
                            repository.findUserByName(nameInput).let {
                                if (it != null) return@launch
                                repository.insertUser(User(name = nameInput, key = keyInput))
                                nameInput = ""
                                keyInput = ""
                            }
                        }
                    }
                },
                enabled = nameInput.isNotBlank() && keyInput.isNotBlank()
            ) {
                Text("添加")
            }
        }
        Spacer(Modifier.height(24.dp))
        Text("用户列表")
        Spacer(Modifier.height(8.dp))
        LazyColumn {
            itemsIndexed(users) { index, user ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(user.name)
                        if (keyVisibleIndex == index) {
                            Text(
                                user.key,
                                color = Color.Gray,
                                modifier = Modifier
                                    .clickable { keyVisibleIndex = null }
                                    .padding(top = 4.dp)
                            )
                        } else {
                            Text(
                                "点击显示Key",
                                color = Color.Gray,
                                modifier = Modifier
                                    .clickable { keyVisibleIndex = index }
                                    .padding(top = 4.dp)
                            )
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                repository.deleteUser(user)
                                if (keyVisibleIndex == index) keyVisibleIndex = null
                                else if (keyVisibleIndex != null && keyVisibleIndex!! > index) keyVisibleIndex = keyVisibleIndex!! - 1
                            }
                        }
                    ) {
                        Text("删除", color = Color.White)
                    }
                }
            }
        }
    }
}
