package cn.xiaowine.lcmanager.ui.pages

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cn.xiaowine.lcmanager.data.database.UserDatabase
import cn.xiaowine.lcmanager.data.entity.AllProduct
import cn.xiaowine.lcmanager.data.entity.OrderProduct
import cn.xiaowine.lcmanager.data.entity.ProductInfo
import cn.xiaowine.lcmanager.data.entity.User
import cn.xiaowine.lcmanager.data.entity.UserPurchaseInfo
import cn.xiaowine.lcmanager.data.network.LcscApi.getOrderList
import cn.xiaowine.lcmanager.data.network.LcscApi.getOrderPart
import cn.xiaowine.lcmanager.ui.component.ErrorDialog
import cn.xiaowine.lcmanager.ui.component.SearchBar
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.InfiniteProgressIndicator
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.basic.ArrowUpDown
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
    var isNetworkErrorDialogVisible = remember { mutableStateOf(false) }

    // 搜索和过滤状态
    var searchQuery by remember { mutableStateOf("") }


    // 获取所有可用的分类选项，需要考虑其他筛选条件的影响
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
                                text = "暂无符合条件的订单",
                                style = MiuixTheme.textStyles.body1,
                                color = Color.Gray
                            )
                        }
                    }
                } else {

                    // 统计卡片
                    DataCard(
                        orderCount = products.value.groupBy { it.orderCode }.size,
                        itemCount = products.value.size,
                    )

                    // 产品列表
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .padding(top = 16.dp)
                    ) {
                        items(groupedProducts.toList()) { (orderCode, products) ->
                            OrderItemFromProducts(orderCode = orderCode, orderProducts = products)
                        }
                    }
                }
            }
        }

        RefreshIcon(
            modifier = Modifier
                .padding(padding)
                .align(Alignment.BottomEnd),
            onRefresh = {
                isLoadDialogVisible.value = true
                coroutineScope.launch {
                    try {
                        fetchOrderData(repository, users.value)
                    } catch (e: Exception) {
                        isNetworkErrorDialogVisible.value = true
                        e.printStackTrace()
                    }
                    isLoadDialogVisible.value = false
                }
            }
        )
    }

    SuperDialog(
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
    ErrorDialog(isNetworkErrorDialogVisible)
}

/**
 * 订单商品项组件
 *
 * @param orderCode 订单编号
 * @param orderProducts 商品列表
 */
@Composable
fun OrderItemFromProducts(orderCode: String, orderProducts: List<OrderProduct>) {
    // 使用第一个产品的用户名，因为同一订单的用户名应该相同
    val userName = orderProducts.firstOrNull()?.userName ?: "未知"

    SelectionContainer {
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
            // 订单头部信息 - 添加用户名和订单编号
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                // 订单编号和用户名行
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = orderCode,
                        style = MiuixTheme.textStyles.body1,
                        modifier = Modifier.padding(start = 4.dp),
                        color = MiuixTheme.colorScheme.primary
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = userName,
                            style = MiuixTheme.textStyles.body1
                        )
                    }
                }

                // 可以添加时间等额外信息
                if (orderProducts.isNotEmpty() && orderProducts.first().orderTime.isNotBlank()) {
                    Text(
                        text = "下单时间: ${orderProducts.first().orderTime}",
                        style = MiuixTheme.textStyles.title4,
                        color = MiuixTheme.colorScheme.onSurfaceVariantActions.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
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
            orderProducts.forEachIndexed { index, product ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = if (index != orderProducts.lastIndex) 8.dp else 0.dp),
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

            // 添加订单总价
            if (orderProducts.isNotEmpty()) {
                val totalPrice = orderProducts.sumOf { it.productPrice * it.purchaseNumber }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .height(0.75.dp)
                        .background(MiuixTheme.colorScheme.dividerLine)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "订单总价: ",
                        style = MiuixTheme.textStyles.body1,
                        color = MiuixTheme.colorScheme.onSurfaceVariantActions
                    )
                    Text(
                        text = "${String.format("%.2f", totalPrice)}元",
                        style = MiuixTheme.textStyles.title2,
                        color = MiuixTheme.colorScheme.primary
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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp)
            .clickable(
                interactionSource = null,
                indication = null
            ) { isExpanded = !isExpanded }
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
    users: List<User>
) {
    println("Start refreshing order data")

    // 1. Clear all existing product data
    println("Cleaning old data...")
    repository.productDao().deleteAllProducts()
    repository.allProductDao().deleteAllProducts()

    var totalOrderCount = 0
    var processedOrderCount = 0

    // 临时Map用于合并相同元件，以产品编码为键，关联用户的购买数据
    val userPurchaseMap = mutableMapOf<String, MutableMap<String, UserPurchaseInfo>>()
    val productInfoMap = mutableMapOf<String, ProductInfo>()

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

                        // Create normal OrderProduct
                        val orderProduct = OrderProduct(
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
                            orderTime = data.orderTime,
                            userName = user.name
                        )
                        repository.productDao().insertProduct(orderProduct)

                        // Merge into AllProduct
                        val productKey = detail.productCode ?: ""
                        if (productKey.isNotEmpty()) {
                            // 保存产品基本信息
                            if (!productInfoMap.containsKey(productKey)) {
                                productInfoMap[productKey] = ProductInfo(
                                    brandName = detail.brandName ?: "",
                                    productCode = detail.productCode ?: "",
                                    breviaryImageUrl = detail.breviaryImageUrl ?: "",
                                    catalogName = detail.catalogName ?: "",
                                    encapStandard = detail.encapStandard ?: "",
                                    productModel = detail.productModel ?: "",
                                    productName = detail.productName ?: "",
                                    productPrice = detail.productPrice ?: 0.0,
                                    stockUnit = detail.stockUnit ?: "",
                                    uuid = detail.uuid ?: "",
                                    orderCode = data.orderCode,
                                    orderUuid = data.uuid,
                                    orderTime = data.orderTime
                                )
                            }

                            // 获取或创建该产品的用户购买映射
                            val userMap = userPurchaseMap.getOrPut(productKey) { mutableMapOf() }

                            // 更新该用户的购买数量
                            val existingInfo = userMap[user.name]
                            if (existingInfo != null) {
                                userMap[user.name] = existingInfo.copy(
                                    purchaseNumber = existingInfo.purchaseNumber + detail.purchaseNumber
                                )
                            } else {
                                userMap[user.name] = UserPurchaseInfo(
                                    userName = user.name,
                                    purchaseNumber = detail.purchaseNumber,
                                    usedNumber = 0
                                )
                            }
                        }
                        println("Inserted product: ${orderProduct.productName}")
                    }
                } else {
                    println("Failed to get order details for ${data.orderCode}, status code: ${orderPart.code}")
                }

            }
        }

    }

    // 构建和插入 AllProduct 实体
    userPurchaseMap.forEach { (productKey, userMap) ->
        val productInfo = productInfoMap[productKey]
        if (productInfo != null) {
            val allProduct = AllProduct(
                brandName = productInfo.brandName,
                productCode = productInfo.productCode,
                breviaryImageUrl = productInfo.breviaryImageUrl,
                catalogName = productInfo.catalogName,
                encapStandard = productInfo.encapStandard,
                productModel = productInfo.productModel,
                productName = productInfo.productName,
                productPrice = productInfo.productPrice,
                stockUnit = productInfo.stockUnit,
                uuid = productInfo.uuid,
                orderCode = productInfo.orderCode,
                orderUuid = productInfo.orderUuid,
                orderTime = productInfo.orderTime,
                userPurchaseInfos = userMap.values.toList()
            )
            repository.allProductDao().insertProduct(allProduct)
        }
    }

    println("Data refresh completed")
    println("Total orders found: $totalOrderCount")
    println("Successfully processed orders: $processedOrderCount")
    println("Total unique products: ${productInfoMap.size}")
}
