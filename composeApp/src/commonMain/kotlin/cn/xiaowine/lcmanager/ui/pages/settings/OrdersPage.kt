package cn.xiaowine.lcmanager.ui.pages.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cn.xiaowine.lcmanager.data.database.UserDatabase
import cn.xiaowine.lcmanager.data.entity.Product
import cn.xiaowine.lcmanager.data.entity.User
import cn.xiaowine.lcmanager.data.network.OrderApi.getOrderList
import cn.xiaowine.lcmanager.data.network.OrderApi.getOrderPart
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.InfiniteProgressIndicator
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.basic.ArrowUpDown
import top.yukonga.miuix.kmp.icon.icons.basic.Search
import top.yukonga.miuix.kmp.icon.icons.useful.Refresh
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape

/**
 * 订单页面主组件
 *
 * @param repository 数据库仓库
 * @param padding 页面内边距
 */
@Composable
fun OrdersPage(repository: UserDatabase, padding: PaddingValues) {
    val coroutineScope = rememberCoroutineScope()
    val products = repository.productDao().getAllProducts().collectAsState(initial = emptyList())
    val users = repository.userDao().getAllUsers().collectAsState(initial = emptyList())

    var isLoadDialogVisible = remember { mutableStateOf(false) }

    // 搜索和过滤状态
    var searchQuery by remember { mutableStateOf("") }

    // 过滤产品
    val filteredProducts = remember(products.value, searchQuery) {
        products.value.filter { product ->
            val matchesSearch = searchQuery.isEmpty() ||
                    product.productName.contains(searchQuery, ignoreCase = true) ||
                    product.productModel.contains(searchQuery, ignoreCase = true)
            matchesSearch
        }
    }

    // 按订单分组过滤后的产品
    val groupedProducts = remember(filteredProducts) {
        filteredProducts.groupBy { it.orderCode }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // 统计卡片
            DataCard(
                orderCount = products.value.groupBy { it.orderCode }.size,
                itemCount = products.value.size,
            )
            SearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .padding(top = 24.dp),

                )

            // 产品列表
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .padding(top = 16.dp)
            ) {
                items(groupedProducts.toList()) { (orderCode, products) ->
                    OrderItemFromProducts(orderCode = orderCode, products = products)
                }
            }
        }

        RefreshIcon(
            modifier = Modifier
                .padding(padding)
                .align(Alignment.BottomEnd),
            onRefresh = {
                coroutineScope.launch {
                    fetchOrderData(repository, users.value, isLoadDialogVisible)
                }
            }
        )
    }

    SuperDialog(
        modifier = Modifier,
        show = isLoadDialogVisible,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "正在缓存最新订单数据...",
                style = MiuixTheme.textStyles.body1,
                color = MiuixTheme.colorScheme.onSurface
            )

            InfiniteProgressIndicator(
                modifier = Modifier.size(48.dp)
            )

            Text(
                text = "请耐心等待，数据加载过程可能需要几分钟",
                style = MiuixTheme.textStyles.subtitle,
                color = MiuixTheme.colorScheme.onSurfaceVariantActions,
                textAlign = TextAlign.Center
            )

        }
    }
}

/**
 * 搜索栏组件
 *
 * @param searchQuery 搜索关键词
 * @param onSearchQueryChange 搜索关键词变化回调
 * @param modifier 组件修饰符
 */
@Composable
private fun SearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(SmoothRoundedCornerShape(12.dp))
            .background(MiuixTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = MiuixIcons.Basic.Search,
            contentDescription = "搜索",
            tint = MiuixTheme.colorScheme.onSurfaceVariantActions
        )
        BasicTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp),
            textStyle = MiuixTheme.textStyles.body1.copy(
                color = MiuixTheme.colorScheme.onSurface
            ),
            singleLine = true,
            decorationBox = { innerTextField ->
                Box {
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = "搜索器件...",
                            style = MiuixTheme.textStyles.body1,
                            color = MiuixTheme.colorScheme.onSurfaceVariantActions
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}


/**
 * 订单商品项组件
 *
 * @param orderCode 订单编号
 * @param products 商品列表
 */
@Composable
fun OrderItemFromProducts(orderCode: String, products: List<Product>) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(SmoothRoundedCornerShape(16.dp))
            .background(
                MiuixTheme.colorScheme.surface
            )
            .border(
                width = 0.75.dp,
                color = MiuixTheme.colorScheme.dividerLine,
                shape = SmoothRoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        // 订单头部信息
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "订单编号",
                style = MiuixTheme.textStyles.body2,
                color = MiuixTheme.colorScheme.onSurfaceVariantActions
            )
            Text(
                text = orderCode,
                style = MiuixTheme.textStyles.body1.copy(
                    color = MiuixTheme.colorScheme.primary
                )
            )
        }

        // 分割线
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .height(0.75.dp)
                .background(MiuixTheme.colorScheme.dividerLine)
        )

        // 商品列表
        products.forEachIndexed { index, product ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = if (index != products.lastIndex) 8.dp else 0.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(0.6f)
                        .padding(end = 16.dp)
                ) {
                    Text(
                        text = product.productModel,
                        style = MiuixTheme.textStyles.body1,
                        color = MiuixTheme.colorScheme.onSurface
                    )
                    Text(
                        text = product.catalogName,
                        style = MiuixTheme.textStyles.title4,
                        color = MiuixTheme.colorScheme.onSurfaceVariantActions,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(0.4f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "${String.format("%.2f", product.productPrice * product.purchaseNumber)}元",
                        style = MiuixTheme.textStyles.body1,
                        color = MiuixTheme.colorScheme.primary
                    )
                    Text(
                        text = "${product.productPrice}元 × ${product.purchaseNumber}",
                        style = MiuixTheme.textStyles.title4,
                        color = MiuixTheme.colorScheme.onSurfaceVariantActions,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}

/**
 * 刷新按钮组件
 *
 * @param modifier 组件修饰符
 * @param onRefresh 刷新回调
 */
@Composable
fun RefreshIcon(
    modifier: Modifier,
    onRefresh: () -> Unit
) {
    IconButton(
        modifier = modifier
            .padding(24.dp)
            .shadow(elevation = 4.dp, SmoothRoundedCornerShape(15.dp))
            .clip(SmoothRoundedCornerShape(15.dp))
            .background(MiuixTheme.colorScheme.background)
            .border(
                width = 0.75.dp,
                color = MiuixTheme.colorScheme.dividerLine,
                shape = SmoothRoundedCornerShape(15.dp)
            ),
        onClick = onRefresh
    ) {
        Icon(
            imageVector = MiuixIcons.Useful.Refresh,
            contentDescription = "刷新"
        )
    }
}

/**
 * 数据统计卡片组件
 *
 * @param orderCount 订单数量
 * @param itemCount 商品数量
 * @param modifier 组件修饰符
 */
@Composable
private fun DataCard(
    orderCount: Int,
    itemCount: Int,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(targetValue = if (isExpanded) 0f else -180f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .padding(top = 24.dp)
            .clip(SmoothRoundedCornerShape(12.dp))
            .background(MiuixTheme.colorScheme.surface)
            .clickable { isExpanded = !isExpanded }
    ) {
        // Header section with arrow
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "统计信息",
                style = MiuixTheme.textStyles.title2,
                color = MiuixTheme.colorScheme.onSurfaceVariantActions
            )
            Icon(
                imageVector = MiuixIcons.Basic.ArrowUpDown,
                contentDescription = "展开/收起",
                modifier = Modifier.graphicsLayer {
                    rotationZ = rotationState
                },
                tint = MiuixTheme.colorScheme.onSurfaceVariantActions
            )
        }

        // Expandable content
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                // 订单数统计
                StatisticItem(
                    value = orderCount.toString(),
                    label = "订单数"
                )

                // 元件数统计
                StatisticItem(
                    value = itemCount.toString(),
                    label = "元件数"
                )
            }
        }
    }
}

/**
 * 统计数据项组件
 *
 * @param value 数值
 * @param label 标签
 * @param modifier 组件修饰符
 */
@Composable
private fun StatisticItem(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(SmoothRoundedCornerShape(8.dp))
            .background(MiuixTheme.colorScheme.secondaryContainer.copy(alpha = 0.1f))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column {
            Text(
                text = value,
                style = MiuixTheme.textStyles.title1,
                color = MiuixTheme.colorScheme.primary
            )
            Text(
                text = label,
                style = MiuixTheme.textStyles.body2,
                color = MiuixTheme.colorScheme.onSurfaceContainerVariant,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

/**
 * 刷新数据的异步函数
 * 功能：获取所有用户的订单数据并更新到数据库
 * 步骤：
 * 1. 清理旧数据
 * 2. 遍历每个用户获取订单列表
 * 3. 获取每个订单的详细信息
 * 4. 保存到数据库
 *
 * @param repository 数据库仓库
 * @param users 用户列表
 */
private suspend fun fetchOrderData(
    repository: UserDatabase,
    users: List<User>,
    isKeyVisible: MutableState<Boolean>
) {
    println("Start refreshing order data")

    try {
        isKeyVisible.value = true
        // 1. Clear all existing product data
        println("Cleaning old data...")
        repository.productDao().deleteAllProducts()

        var totalOrderCount = 0
        var processedOrderCount = 0

        // 2. Fetch new data for each user
        users.forEach { user ->
            println("Processing user: ${user.name}")

            // Get first page and total pages
            val firstPage = getOrderList(user.key)
            if (firstPage.code != 200) {
                println("Failed to get user order list, status code: ${firstPage.code}")
                return@forEach
            }

            val totalPages = firstPage.result!!.totalPage
            println("Total pages: $totalPages")

            // Process all pages
            for (page in 1..totalPages) {
                println("Fetching page $page")
                val orderList = getOrderList(user.key, currPage = page)

                // Count total orders for verification
                totalOrderCount += orderList.result?.dataList?.size ?: 0

                // Process each order
                orderList.result?.dataList?.forEach { data ->
                    println("Getting order details: ${data.orderCode}")
                    val orderPart = getOrderPart(user.key, data.uuid)

                    if (orderPart.code == 200) {

                        // Process each product in the order
                        val products = orderPart.result!!.szProductList.ifEmpty { orderPart.result.jsProductList }
                        products.forEach { detail ->
                            processedOrderCount++
                            // Create product even if purchaseNumber is null, it will be set to 0
                            val product = Product(
                                brandName = detail.brandName ?: "",
                                productCode = detail.productCode ?: "",
                                breviaryImageUrl = detail.breviaryImageUrl ?: "",
                                catalogName = detail.catalogName ?: "",
                                encapStandard = detail.encapStandard ?: "",
                                productModel = detail.productModel ?: "",
                                productName = detail.productName ?: "",
                                productPrice = detail.productPrice ?: 0.0,
                                purchaseNumber = detail.purchaseNumber,
                                stockUnit = detail.stockUnit ?: "",
                                uuid = detail.uuid ?: "",
                                orderCode = data.orderCode,
                                orderUuid = data.uuid,
                                orderTime = data.orderTime
                            )
                            repository.productDao().insertProduct(product)
                            println("Inserted product: ${product.productName}")
                        }
                    } else {
                        println("Failed to get order details for ${data.orderCode}, status code: ${orderPart.code}")
                    }
                }
            }
        }

        println("Data refresh completed")
        println("Total orders found: $totalOrderCount")
        println("Successfully processed orders: $processedOrderCount")
    } catch (e: Exception) {
        println("Error refreshing data: ${e.message}")
        e.printStackTrace()
    }
    isKeyVisible.value = false
}
