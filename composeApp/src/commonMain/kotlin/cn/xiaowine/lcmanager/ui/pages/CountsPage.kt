package cn.xiaowine.lcmanager.ui.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.xiaowine.lcmanager.data.database.UserDatabase
import cn.xiaowine.lcmanager.ui.component.SearchBar
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * 订单页面主组件
 *
 * @param repository 数据库仓库
 * @param padding 页面内边距
 */
@Composable
fun CountsPage(repository: UserDatabase, padding: PaddingValues) {

    val coroutineScope = rememberCoroutineScope()
    val products = repository.productDao().getAllProducts().collectAsState(initial = emptyList())

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
            SearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .padding(top = 24.dp),
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .padding(top = 16.dp)
            ) {
                items(groupedProducts.toList()) { (productCode, products) ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "产品编码: $productCode",
                                style = MiuixTheme.textStyles.title3
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // 显示第一个产品的基本信息（因为同productCode的产品基本信息应该相同）
                            val firstProduct = products.first()
                            Text(text = "产品名称: ${firstProduct.productName}")
                            Text(text = "品牌: ${firstProduct.brandName}")
                            Text(text = "型号: ${firstProduct.productModel}")
                            Spacer(modifier = Modifier.height(8.dp))

                            // 显示总数量和总价格
                            val totalQuantity = products.sumOf { it.purchaseNumber }
                            val totalPrice = products.sumOf { it.productPrice * it.purchaseNumber }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "总数量: $totalQuantity ${firstProduct.stockUnit}",
                                    style = MiuixTheme.textStyles.body2
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = "总价: ¥${String.format("%.2f", totalPrice)}",
                                    style = MiuixTheme.textStyles.body2
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

