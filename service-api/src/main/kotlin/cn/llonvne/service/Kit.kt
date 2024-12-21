@file:OptIn(DelicateCoroutinesApi::class)

package cn.llonvne.service

import cn.llonvne.gateway.ApiInsightRequest
import cn.llonvne.gateway.ApiInsightResponse
import cn.llonvne.gateway.ApiWebSocketPacket
import cn.llonvne.gateway.HttpMethod
import cn.llonvne.gateway.Ping
import cn.llonvne.gateway.ServiceInsight
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.converter
import io.ktor.client.plugins.websocket.sendSerialized
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.util.reflect.typeInfo
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

interface Kit {
    val gatewayHost: String

    val client: HttpClient

    val serviceInsight: ServiceInsight

    val gatewayPort: Int

    val server: EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration>
}

class KitImpl(
    override val gatewayHost: String,
    override val gatewayPort: Int,
    val port: Int,
    val action: ServiceInsightWithAction,
    override val client: HttpClient =
        HttpClient(CIO) {
            install(ContentNegotiation) {
                this.json(Json)
            }
            install(WebSockets) {
                contentConverter = KotlinxWebsocketSerializationConverter(Json)
                maxFrameSize = Long.MAX_VALUE
            }
        },
) : Kit {

    override val serviceInsight: ServiceInsight = action.toServiceInsight()

    override val server = embeddedServer(Netty, port = port) {
        routing {
            route(action.baseUri) {
                action.apis.forEach {
                    when (it.apiDescriptor.method) {
                        HttpMethod.GET -> get(it.apiDescriptor.localUri, it.action)

                        HttpMethod.POST -> post(it.apiDescriptor.localUri, it.action)

                        HttpMethod.DELETE -> delete(it.apiDescriptor.localUri, it.action)

                        HttpMethod.PATCH -> patch(it.apiDescriptor.localUri, it.action)
                    }
                }
            }
        }
    }.start()

    private val logger = LoggerFactory.getLogger("Kit for gateway $gatewayHost")

    @OptIn(ExperimentalSerializationApi::class)
    private val jsonPrinter = Json {
        encodeDefaults = true
        prettyPrint = true
        prettyPrintIndent = "  "
    }

    private suspend fun DefaultClientWebSocketSession.processPacket(packet: ApiWebSocketPacket) {
        when (packet) {
            is ApiInsightRequest -> sendSerialized(processApiInsightRequest(packet))
            is Ping -> Unit
            is ApiInsightResponse -> Unit
        }
    }

    private fun processApiInsightRequest(request: ApiInsightRequest): ApiWebSocketPacket {
        logger.info("received ApiInsightRequest")

        logger.info(
            "send ${
                jsonPrinter.encodeToString(serviceInsight)
            }"
        )

        return ApiInsightResponse(
            serviceInsight,
        )
    }

    suspend fun start() {
        while (true) {
            try {
                client.webSocket(
                    method = io.ktor.http.HttpMethod.Get,
                    host = gatewayHost,
                    port = gatewayPort,
                    path = "/websocket/api",
                    request = {
                        headers.append("service_name", serviceInsight.name)
                    },
                ) {
                    for (frame in incoming) {
                        val resp = converter?.deserialize(Charsets.UTF_8, typeInfo<ApiWebSocketPacket>(), frame)

                        if (resp != null) {
                            resp as ApiWebSocketPacket

                            processPacket(resp)
                        }
                    }
                }
            } catch (e: Exception) {
                logger.error("WebSocket connection failed or ended, attempting to reconnect in 5 seconds", e)
                delay(5_000L) // Wait for 30 seconds before retrying
            }
        }
    }
}
