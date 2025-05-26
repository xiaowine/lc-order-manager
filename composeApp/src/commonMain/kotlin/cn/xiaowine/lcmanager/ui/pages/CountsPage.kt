package cn.xiaowine.lcmanager.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape

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
    var selectedCatalog by remember { mutableStateOf<String?>(null) }
    var selectedEncap by remember { mutableStateOf<String?>(null) }
    var selectedBrand by remember { mutableStateOf<String?>(null) }

    // 对话框状态
    val showCatalogDialog = remember { mutableStateOf(false) }
    val showEncapDialog = remember { mutableStateOf(false) }
    val showBrandDialog = remember { mutableStateOf(false) }

    // 获取所有可用的分类选项，需要考虑其他筛选条件的影响
    val filteredProducts = remember(products.value, searchQuery, selectedCatalog, selectedEncap, selectedBrand) {
        products.value.filter { product ->
            val matchesSearch = searchQuery.isEmpty() ||
                    product.productName.contains(searchQuery, ignoreCase = true) ||
                    product.productCode.contains(searchQuery, ignoreCase = true)
            val matchesCatalog = selectedCatalog == null || product.catalogName == selectedCatalog
            val matchesEncap = selectedEncap == null || product.encapStandard == selectedEncap
            val matchesBrand = selectedBrand == null || product.brandName == selectedBrand

            matchesSearch && matchesCatalog && matchesEncap && matchesBrand
        }
    }

    // 基于当前选择更新可选项列表
    val catalogs = remember(filteredProducts) {
        filteredProducts
            .filter { product ->
                (selectedEncap == null || product.encapStandard == selectedEncap) &&
                        (selectedBrand == null || product.brandName == selectedBrand)
            }
            .map { it.catalogName }
            .distinct()
            .sorted()
    }

    val encaps = remember(filteredProducts) {
        filteredProducts
            .filter { product ->
                (selectedCatalog == null || product.catalogName == selectedCatalog) &&
                        (selectedBrand == null || product.brandName == selectedBrand)
            }
            .map { it.encapStandard }
            .distinct()
            .sorted()
    }

    val brands = remember(filteredProducts) {
        filteredProducts
            .filter { product ->
                (selectedCatalog == null || product.catalogName == selectedCatalog) &&
                        (selectedEncap == null || product.encapStandard == selectedEncap)
            }
            .map { it.brandName }
            .distinct()
            .sorted()
    }

    // 按productCode分组
    val groupedProducts = remember(filteredProducts) {
        filteredProducts.groupBy { it.productCode }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (products.value.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "暂无数据",
                    style = MiuixTheme.textStyles.body1,
                    color = Color.Gray
                )
            }
        } else {
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

                if (filteredProducts.isEmpty()) {
                    // 如果筛选后没有结果，显示提示信息
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "暂无符合条件的商品",
                                style = MiuixTheme.textStyles.body1,
                                color = Color.Gray
                            )
                            if (selectedCatalog != null || selectedEncap != null || selectedBrand != null) {
                                TextButton(
                                    text = "清除筛选",
                                    onClick = {
                                        selectedCatalog = null
                                        selectedEncap = null
                                        selectedBrand = null
                                    },
                                    colors = ButtonDefaults.textButtonColorsPrimary(),
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }
                } else {
                    // 筛选器区域
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 目录筛选
                        FilterChip(
                            text = selectedCatalog ?: "目录",
                            selected = selectedCatalog != null,
                            onClick = {
                                if (selectedCatalog != null) {
                                    selectedCatalog = null
                                } else {
                                    showCatalogDialog.value = true
                                }
                            },
                            modifier = Modifier.weight(1f).padding(end = 4.dp)
                        )

                        // 封装筛选
                        FilterChip(
                            text = selectedEncap ?: "封装",
                            selected = selectedEncap != null,
                            onClick = {
                                if (selectedEncap != null) {
                                    selectedEncap = null
                                } else {
                                    showEncapDialog.value = true
                                }
                            },
                            modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                        )

                        // 品牌筛选
                        FilterChip(
                            text = selectedBrand ?: "品牌",
                            selected = selectedBrand != null,
                            onClick = {
                                if (selectedBrand != null) {
                                    selectedBrand = null
                                } else {
                                    showBrandDialog.value = true
                                }
                            },
                            modifier = Modifier.weight(1f).padding(start = 4.dp)
                        )
                    }

                    // 选择对话框
                    SelectionDialog(
                        title = "选择目录",
                        show = showCatalogDialog,
                        items = catalogs,
                        onItemSelected = { selectedCatalog = it }
                    )

                    SelectionDialog(
                        title = "选择封装",
                        show = showEncapDialog,
                        items = encaps,
                        onItemSelected = { selectedEncap = it }
                    )

                    SelectionDialog(
                        title = "选择品牌",
                        show = showBrandDialog,
                        items = brands,
                        onItemSelected = { selectedBrand = it }
                    )

                    // 产品列表
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 20.dp)
                            .padding(top = 12.dp, bottom = 16.dp)
                    ) {
                        val cardMinWidth = 300.dp
                        val containerWidth = maxWidth - 40.dp // 减去水平padding
                        val cardsPerRow = maxOf(1, (containerWidth / cardMinWidth).toInt())
                        val itemsList = groupedProducts.toList()

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(itemsList.size / cardsPerRow + if (itemsList.size % cardsPerRow > 0) 1 else 0) { rowIndex ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    for (columnIndex in 0 until cardsPerRow) {
                                        val index = rowIndex * cardsPerRow + columnIndex
                                        if (index < itemsList.size) {
                                            val (productCode, products) = itemsList[index]
                                            ProductCard(
                                                productCode = productCode,
                                                products = products,
                                                repository = repository,
                                                modifier = Modifier.weight(1f)
                                            )
                                        } else {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductCard(
    productCode: String,
    products: List<AllProduct>,
    repository: UserDatabase,
    modifier: Modifier = Modifier
) {
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
        modifier = modifier
            .widthIn(min = 300.dp, max = 450.dp)
            .padding(vertical = 4.dp),
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
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 产品图片
                Box(
                    modifier = Modifier.size(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    SubcomposeAsyncImage(
                        model = firstProduct.breviaryImageUrl,
                        contentDescription = firstProduct.productName,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(80.dp),
                        loading = {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(20.dp)
                                    .padding(30.dp)
                            )
                        }
                    )
                }

                // 产品信息
                SelectionContainer {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp)
                    ) {
                        // 产品名称和编码
                        Text(
                            text = firstProduct.productName,
                            style = MiuixTheme.textStyles.title4.copy(fontWeight = FontWeight.Bold),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = productCode,
                            style = MiuixTheme.textStyles.body2,
                            color = Color.Gray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = Color.LightGray,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )

                        // 产品详情信息
                        Row {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = firstProduct.brandName,
                                    style = MiuixTheme.textStyles.body2,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = firstProduct.encapStandard,
                                    style = MiuixTheme.textStyles.body2,
                                    modifier = Modifier.padding(top = 2.dp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = firstProduct.productName,
                                    style = MiuixTheme.textStyles.body2,
                                    modifier = Modifier.padding(top = 2.dp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Column(
                                modifier = Modifier.padding(start = 8.dp),
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = "${firstProduct.totalPurchaseNumber}${firstProduct.stockUnit}",
                                    style = MiuixTheme.textStyles.body2
                                )
                                Text(
                                    text = firstProduct.totalUsedNumber.let { if (it == 0) "未消耗" else "- $it" },
                                    style = MiuixTheme.textStyles.body2
                                )
                                Text(
                                    text = "¥${String.format("%.2f", products.sumOf { it.productPrice * it.totalPurchaseNumber })}",
                                    style = MiuixTheme.textStyles.body2,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun SelectionDialog(
    title: String,
    show: MutableState<Boolean>,
    items: List<String>,
    onItemSelected: (String) -> Unit
) {
    SuperDialog(
        title = title,
        show = show,
        onDismissRequest = { show.value = false }
    ) {
        val verticalScroll = rememberScrollState(0)
        Column(
            modifier = Modifier
                .heightIn(max = 500.dp)
                .padding(vertical = 8.dp)
        ) {
            FlowRow(
                modifier = Modifier.verticalScroll(verticalScroll),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items.forEach {
                    Row(
                        modifier = Modifier
                            .clip(SmoothRoundedCornerShape(12.dp))
                            .background(Color(0xFFE6E6E6))
                            .clickable {
                                show.value = false
                                onItemSelected(it)
                            }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            maxLines = 1,
                            text = it,
                            fontSize = 14.sp,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
//            LazyColumn {
//                items(items) { item ->
//                    TextButton(
//                        text = item,
//                        onClick = {
//                            onItemSelected(item)
//                            show.value = false
//                        },
//                        modifier = Modifier.padding(vertical = 5.dp),
//                        colors = ButtonDefaults.textButtonColors(),
//                    )
//                }
//            }
        }
        TextButton(
            text = "确定",
            onClick = { show.value = false },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = ButtonDefaults.textButtonColorsPrimary()
        )
    }
}


@Composable
fun FilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (selected) MiuixTheme.colorScheme.primary.copy(alpha = 0.1f)
                else Color.Transparent
            )
            .border(
                width = 1.dp,
                color = if (selected) MiuixTheme.colorScheme.primary
                else Color.Gray.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MiuixTheme.textStyles.body2,
            color = if (selected)
                MiuixTheme.colorScheme.primary
            else
                Color.Gray
        )
    }
}
