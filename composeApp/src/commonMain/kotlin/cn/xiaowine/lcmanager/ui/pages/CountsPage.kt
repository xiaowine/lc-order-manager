package cn.xiaowine.lcmanager.ui.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.xiaowine.lcmanager.data.database.UserDatabase
import cn.xiaowine.lcmanager.data.entity.AllProduct
import cn.xiaowine.lcmanager.ui.component.SearchBar
import coil3.compose.SubcomposeAsyncImage
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CircularProgressIndicator
import top.yukonga.miuix.kmp.basic.HorizontalDivider
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * 订单页面主组件
 *
 * @param repository 数据库仓库
 * @param padding 页面内边距
 */
@Composable
fun CountsPage(repository: UserDatabase, padding: PaddingValues) {
    val products = repository.allProductDao().getAllProducts().collectAsState(initial = emptyList())

    // 搜索和过滤状态
    var searchQuery by remember { mutableStateOf("") }

    // 按productCode分组
    val groupedProducts = products.value
        .filter { product ->
            searchQuery.isEmpty() ||
                    product.productName.contains(searchQuery, ignoreCase = true) ||
                    product.productCode.contains(searchQuery, ignoreCase = true)
        }
        .groupBy { it.productCode }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 搜索栏
            SearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 16.dp),
            )

            // 产品列表
            if (products.value.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "暂无数据",
                        style = MiuixTheme.textStyles.body1.copy(color = Color.Gray)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(top = 12.dp, bottom = 16.dp)
                ) {
                    items(groupedProducts.toList()) { (productCode, products) ->
                        ProductCard(productCode, products, repository)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductCard(productCode: String, products: List<AllProduct>, repository: UserDatabase) {
    val firstProduct = products.first()
    val coroutineScope = rememberCoroutineScope()
    val remainingStock = firstProduct.totalPurchaseNumber - firstProduct.totalUsedNumber

    // 添加状态管理
    var showDialog = remember { mutableStateOf(false) }
    var totalUsedNumber by remember { mutableStateOf("") }
    var incrementalUsage by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    // 弹出对话框
    SuperDialog(
        show = showDialog,
        title = firstProduct.productName,
        onDismissRequest = { showDialog.value = false },
        content = {
            // 初始化总使用量
            if (showDialog.value && totalUsedNumber.isEmpty()) {
                totalUsedNumber = firstProduct.totalUsedNumber.toString()
            }

            Column(modifier = Modifier.padding(16.dp)) {
                SelectionContainer {
                    Column {
                        // 产品详细信息展示
                        Text(
                            text = "产品编号: ${firstProduct.productCode}",
                            style = MiuixTheme.textStyles.body2
                        )

                        Text(
                            text = "品牌: ${firstProduct.brandName}",
                            style = MiuixTheme.textStyles.body2,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        Text(
                            text = "型号: ${firstProduct.productModel}",
                            style = MiuixTheme.textStyles.body2,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Text(
                            text = "封装: ${firstProduct.encapStandard}",
                            style = MiuixTheme.textStyles.body2,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Text(
                            text = "目录: ${firstProduct.catalogName}",
                            style = MiuixTheme.textStyles.body2,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Text(
                            text = "单价: ¥${String.format("%.2f", firstProduct.productPrice)}",
                            style = MiuixTheme.textStyles.body2,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = Color.LightGray,
                            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                        )

                        // 库存信息
                        Text(
                            text = "总数量: ${firstProduct.totalPurchaseNumber} ${firstProduct.stockUnit}",
                            style = MiuixTheme.textStyles.body1.copy(fontWeight = FontWeight.Bold)
                        )

                        Text(
                            text = "已用数量: ${firstProduct.totalUsedNumber} ${firstProduct.stockUnit}",
                            style = MiuixTheme.textStyles.body1,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Text(
                            text = "剩余数量: $remainingStock ${firstProduct.stockUnit}",
                            style = MiuixTheme.textStyles.body1.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                HorizontalDivider(
                    thickness = 0.5.dp,
                    color = Color.LightGray,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )

                // 总使用量输入
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = "总使用量",
                        style = MiuixTheme.textStyles.title4,
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    TextField(
                        value = totalUsedNumber,
                        onValueChange = { input ->
                            if (input.isEmpty() || input.all { it.isDigit() }) {
                                val number = input.toIntOrNull() ?: 0
                                if (number <= firstProduct.totalPurchaseNumber) {
                                    totalUsedNumber = input
                                    incrementalUsage = "" // 清空增量输入
                                    errorMessage = ""
                                } else {
                                    errorMessage = "使用量不能超过总库存"
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp),
                    )

                    Text(
                        text = firstProduct.stockUnit,
                        style = MiuixTheme.textStyles.title4,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                // 添加使用量输入（增量）
                HorizontalDivider(
                    thickness = 0.5.dp,
                    color = Color.LightGray,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "新增使用",
                        style = MiuixTheme.textStyles.title4,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    TextField(
                        value = incrementalUsage,
                        onValueChange = { input ->
                            if (input.isEmpty() || input.all { it.isDigit() }) {
                                val increment = input.toIntOrNull() ?: 0
                                val currentTotal = totalUsedNumber.toIntOrNull() ?: 0
                                val newTotal = (currentTotal - (incrementalUsage.toIntOrNull() ?: 0)) + increment

                                if (newTotal <= firstProduct.totalPurchaseNumber) {
                                    incrementalUsage = input
                                    totalUsedNumber = newTotal.toString()
                                    errorMessage = ""
                                } else {
                                    errorMessage = "增加后的使用量超过总库存"
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp),
                    )

                    Text(
                        text = firstProduct.stockUnit,
                        style = MiuixTheme.textStyles.title4,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                // 显示错误信息
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        style = MiuixTheme.textStyles.body2,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            TextButton(
                text = "确定",
                onClick = {
                    val finalUsedNumber = totalUsedNumber.toIntOrNull() ?: 0
                    if (finalUsedNumber >= 0 && finalUsedNumber <= firstProduct.totalPurchaseNumber) {
                        coroutineScope.launch {
                            val updatedPurchaseInfos = firstProduct.userPurchaseInfos.map {
                                it.copy(usedNumber = finalUsedNumber)
                            }
                            val updatedProduct = firstProduct.copy(
                                userPurchaseInfos = updatedPurchaseInfos
                            )
                            repository.allProductDao().updateProduct(updatedProduct)
                        }
                    }
                    showDialog.value = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = ButtonDefaults.textButtonColorsPrimary()
            )
        }
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        showIndication = true,
        onClick = {
            // 点击时清空输入和错误信息
            totalUsedNumber = firstProduct.totalUsedNumber.toString()
            incrementalUsage = ""
            errorMessage = ""
            showDialog.value = true
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            if (remainingStock == 0) {
                Text(
                    text = "耗尽",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 12.dp, top = 14.dp)
                        .rotate(45f),
                    color = Color.Red
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 产品图片
                Box(
                    modifier = Modifier.size(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    SubcomposeAsyncImage(
                        model = firstProduct.breviaryImageUrl,
                        contentDescription = firstProduct.productName,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(100.dp),
                        loading = {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(38.dp)
                            )
                        }
                    )
                }

                // 产品信息 - 使用SelectionContainer包裹，使文本可选择复制
                SelectionContainer {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp)
                    ) {
                        // 产品名称和编码
                        Text(
                            text = firstProduct.productName,
                            style = MiuixTheme.textStyles.title4.copy(fontWeight = FontWeight.Bold)
                        )

                        Text(
                            text = "编号: $productCode",
                            style = MiuixTheme.textStyles.body2.copy(color = Color.Gray)
                        )

                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = Color.LightGray,
                            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                        )

                        // 产品详情信息
                        Row {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "品牌: ${firstProduct.brandName}",
                                    style = MiuixTheme.textStyles.body2
                                )
                                Text(
                                    text = "封装: ${firstProduct.encapStandard}",
                                    style = MiuixTheme.textStyles.body2,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                Text(
                                    text = "型号: ${firstProduct.productModel}",
                                    style = MiuixTheme.textStyles.body2,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }

                            Column(
                                modifier = Modifier.padding(start = 8.dp),
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = "${firstProduct.totalPurchaseNumber} ${firstProduct.stockUnit}",
                                    style = MiuixTheme.textStyles.body2
                                )
                                Text(
                                    text = firstProduct.totalUsedNumber.let { if (it == 0) "未消耗" else "- $it" },
                                    style = MiuixTheme.textStyles.body2,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                Text(
                                    text = "¥${String.format("%.2f", products.sumOf { it.productPrice * it.totalPurchaseNumber })}",
                                    style = MiuixTheme.textStyles.body1,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

