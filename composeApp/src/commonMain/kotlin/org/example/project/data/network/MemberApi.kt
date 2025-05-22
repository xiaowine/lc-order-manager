package org.example.project.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.example.project.data.json.InfoData

object MemberApi {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    suspend fun getCustomerInfo(accessToken: String): InfoData {
        return client.get("https://member.szlcsc.com/phone/cus/customer/info") {
            headers {
                append("origin", "https://m.szlcsc.com")
                append("referer", "https://m.szlcsc.com/")
                append("user-agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 16_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.6 Mobile/15E148 Safari/604.1 Edg/136.0.0.0")
                append("x-lc-accesstoken", accessToken)
                append("x-lc-source", "h5")
            }
        }.body()
    }
}
