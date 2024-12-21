package cn.llonvne.service

import cn.llonvne.gateway.ApiDescriptor
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
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.reflect.typeInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

interface Kit {
    val gatewayHost: String

    val client: HttpClient

    val serviceInsight: ServiceInsight
}

private class KitImpl(
    override val gatewayHost: String,
    override val serviceInsight: ServiceInsight,
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
    private val logger = LoggerFactory.getLogger("Kit at $gatewayHost")

    @OptIn(ExperimentalSerializationApi::class)
    private val jsonPrinter = Json {
        encodeDefaults = true
        prettyPrint = true
        prettyPrintIndent = "  "
    }

    private suspend fun DefaultClientWebSocketSession.processPacket(packet: ApiWebSocketPacket) {
        when (packet) {
            is ApiInsightRequest -> processApiInsightRequest(packet)
            is Ping -> Unit
            is ApiInsightResponse -> Unit
        }
    }

    private suspend fun processApiInsightRequest(request: ApiInsightRequest): ApiWebSocketPacket {
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
                    host = "localhost",
                    port = 8080,
                    path = "/websocket/api",
                    request = {
                        headers.append("service_name", "TestService")
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

fun main() =
    runBlocking {
        val kit =
            KitImpl(
                "localhost:8080",
                ServiceInsight(
                    "TestService",
                    "/api",
                    namespace = "test",
                    listOf(
                        ApiDescriptor(
                            "hello",
                            "/hello",
                            description = "Hello",
                            method = HttpMethod.GET,
                            contentType = "application/json",
                        ),
                    ),
                ),
            )

        kit.start()
    }
