package cn.llonvne.service

import cn.llonvne.gateway.Constants
import cn.llonvne.gateway.HttpMethod
import cn.llonvne.gateway.HttpMethod.DELETE
import cn.llonvne.gateway.HttpMethod.GET
import cn.llonvne.gateway.HttpMethod.PATCH
import cn.llonvne.gateway.HttpMethod.POST
import cn.llonvne.gateway.event.ApiCallEvent
import cn.llonvne.gateway.event.GatewayEvent
import cn.llonvne.service.abc.GatewayServiceBase
import cn.llonvne.service.type.RemoteServiceContext
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsBytes
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.response.respondBytes
import kotlinx.serialization.json.Json

class ApiCallService(
    private val httpClient: HttpClient =
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json)
            }
        },
) : GatewayServiceBase() {
    override val name: String = "ApiCallService"

    override suspend fun collectGateway(gatewayEvent: GatewayEvent) {
        process<ApiCallEvent>(gatewayEvent) { e ->
            val apiDescriptor = e.context.apiDescriptor

            e.routingContext.call.attributes
                .put(Constants.AttributeKeys.remoteServiceAttributeKey, e.context)

            val resp = requestTo(e.context)

            e.routingContext.call.respondBytes(
                status = resp.status,
                contentType = ContentType.parse(apiDescriptor.contentType),
            ) {
                resp.bodyAsBytes()
            }

            e.ok()
        }
    }

    private suspend fun requestTo(context: RemoteServiceContext): HttpResponse {
        val yamlConfig = context.config
        val service = context.insight
        val api = context.apiDescriptor

        // 发送 HTTP 请求
        return httpClient.request(
            yamlConfig.url + service.baseUri + api.localUri,
        ) {
            method = api.method.toHttpMethod()
            contentType(ContentType.parse(api.contentType))
        }
    }

    companion object {
        fun HttpMethod.toHttpMethod(): io.ktor.http.HttpMethod =
            when (this) {
                GET -> io.ktor.http.HttpMethod.Get
                POST -> io.ktor.http.HttpMethod.Post
                DELETE -> io.ktor.http.HttpMethod.Delete
                PATCH -> io.ktor.http.HttpMethod.Patch
            }
    }
}
