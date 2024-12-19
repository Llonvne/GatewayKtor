package cn.llonvne.gateway.config

import cn.llonvne.gateway.ApiWebSocketPacket
import cn.llonvne.gateway.Ping
import cn.llonvne.gateway.event.GatewayEventsCentral
import cn.llonvne.service.AliveDetectService
import cn.llonvne.service.ApiCallService
import cn.llonvne.service.ApiDescriptorService
import cn.llonvne.service.ApiInsightService
import cn.llonvne.service.ApiRouteService
import cn.llonvne.service.ApiWebsocketService
import cn.llonvne.service.BootUpService
import cn.llonvne.service.GatewayService
import cn.llonvne.service.Service
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import io.ktor.util.AttributeKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import java.time.Duration
import kotlin.time.toKotlinDuration

class ApiGatewayConfig {
    // ASYNC COROUTINE SCOPE
    val pluginScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // EVENT
    val eventsCentral = GatewayEventsCentral(pluginScope)

    var gateWayServiceApiRoot = "/gateway"

    // API INSIGHT HTTP CLIENT
    var apiInsightHttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            this.json(Json)
        }
        install(HttpRequestRetry) {
            retryOnException(maxRetries = 3)
            exponentialDelay()

            modifyRequest {
                val req = it
                val context = req.attributes.getOrNull(serviceYamlConfigAttributeKey) ?: return@modifyRequest
                LoggerFactory.getLogger(this@modifyRequest::class.java).warn(
                    "Unable to fetch insight for the service '${context.name}' from '${context.url + context.insightUri}'. " + "Reason: ${this.cause?.localizedMessage}. A retry will be attempted shortly.",
                )
            }
        }
    }

    // API INSIGHT CONFIG ATTRIBUTE KEY
    var serviceYamlConfigAttributeKey = AttributeKey<GatewayServiceYamlConfig>("serviceConfigKey")

    // BASE SERVICES
    val baseServices: MutableList<GatewayService> = mutableListOf(
        ApiRouteService { eventsCentral.emit(it) },
        ApiInsightService(apiInsightHttpClient, serviceYamlConfigAttributeKey) { eventsCentral.emit(it) },
        BootUpService { handler ->
            eventsCentral.collect(handler)
        },
        ApiCallService(),
        ApiDescriptorService { eventsCentral.emit(it) },
        ApiWebsocketService({
            eventsCentral.emit(it)
        }, {
            eventsCentral.emit(it)
        }),
        AliveDetectService(
            httpClient = HttpClient(CIO) {
                install(ContentNegotiation) {
                    json(Json)
                }
            }
        ) {
            eventsCentral.emit(it)
        })

    // CONFIG
    var configGetter: GatewayConfigGetter = GatewayConfigYamlGetter("gateway.yaml")

    // WEBSOCKET
    val webSocketOptions: WebSockets.WebSocketOptions.() -> Unit = {
        contentConverter = KotlinxWebsocketSerializationConverter(Json {
            this.serializersModule = SerializersModule {
                polymorphic(ApiWebSocketPacket::class, Ping::class, Ping.serializer())
            }
        })

        pingPeriod = Duration.ofSeconds(15).toKotlinDuration()

        timeout = Duration.ofSeconds(15).toKotlinDuration()

        maxFrameSize = Long.MAX_VALUE

        masking = false
    }

    val webSocketEndpoint = "/websocket"

    // LOGGING
    var level = Level.INFO

    var apiRoot = "/api"

    internal val services: MutableList<Service> = mutableListOf()

    fun registerLocalService(service: Service) {
        services.add(service)
    }
}
