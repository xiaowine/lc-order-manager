package cn.xiaowine.lcmanager.data.network

import OrderDetailListData
import cn.xiaowine.lcmanager.data.json.InfoData
import cn.xiaowine.lcmanager.data.json.OrderListData
import cn.xiaowine.lcmanager.data.json.OrderProductData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object LcscApi {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    private const val orderBaseUrl = "https://order-api.szlcsc.com"
    private const val memberBaseUrl = "https://member.szlcsc.com"

    private fun commonHeaders(builder: io.ktor.client.request.HttpRequestBuilder, accessToken: String) {
        builder.headers {
            append("origin", "https://m.szlcsc.com")
            append("referer", "https://m.szlcsc.com/")
            append("user-agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 16_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.6 Mobile/15E148 Safari/604.1 Edg/136.0.0.0")
            append("x-lc-accesstoken", accessToken)
            append("x-lc-source", "h5")
        }
    }

    suspend fun getOrderList(
        accessToken: String,
        currPage: Int = 1,
        pageSize: Int = 20,
        orderStatus: String = "already_send"
    ): OrderListData {
        return client.get("$orderBaseUrl/phone/cus/order/list") {
            commonHeaders(this, accessToken)
            url {
                parameters.append("currPage", currPage.toString())
                parameters.append("pageSize", pageSize.toString())
                parameters.append("orderStatus", orderStatus)
            }
        }.body()
    }

    suspend fun getOrderDetail(
        accessToken: String,
        uuid: String
    ): OrderDetailListData {
        return client.get("$orderBaseUrl/phone/cus/order/detail") {
            commonHeaders(this, accessToken)
            url {
                parameters.append("uuid", uuid)
            }
        }.body()
    }

    suspend fun getOrderPart(
        accessToken: String,
        uuid: String
    ): OrderProductData {
        return client.get("$orderBaseUrl/phone/order/product/part") {
            commonHeaders(this, accessToken)
            url {
                parameters.append("uuid", uuid)
            }
        }.body()
    }

    suspend fun getCustomerInfo(accessToken: String): InfoData {
        return client.get("$memberBaseUrl/phone/cus/customer/info") {
            commonHeaders(this, accessToken)
        }.body()
    }
}
