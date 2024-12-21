package cn.llonvne.service

import cn.llonvne.gateway.ApiWebSocketPacket
import cn.llonvne.gateway.event.GatewayEvent
import cn.llonvne.gateway.event.WebSocketInstalled
import cn.llonvne.gateway.event.WebSocketListening
import cn.llonvne.gateway.event.WebsocketApiEvent
import cn.llonvne.gateway.event.WebsocketEstablishedEvent
import cn.llonvne.gateway.type.Emitter
import cn.llonvne.service.abc.GatewayService
import io.ktor.server.request.header
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.converter
import io.ktor.server.websocket.sendSerialized
import io.ktor.server.websocket.webSocket
import io.ktor.util.reflect.typeInfo
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import org.slf4j.LoggerFactory

class ApiWebsocketService(
    val emitter: Emitter<WebsocketApiEvent>,
    val serviceEmitter: Emitter<WebSocketListening>,
    val socketOpenEmitter: Emitter<WebsocketEstablishedEvent>,
) : GatewayService {
    override val name: String = "ApiWebsocketService"

    private val logger = LoggerFactory.getLogger(this::class.java)

    override suspend fun collect(gatewayEvent: GatewayEvent) =
        process<WebSocketInstalled>(gatewayEvent) {
            val root = it.webSocketRoot

            root.webSocket("/api") {
                val serviceName = call.request.header("service_name")
                if (serviceName == null) {
                    close(
                        CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Service name not register in config."),
                    )

                    logger.warn("service $serviceName not register in config.")
                    return@webSocket
                }

                socketOpenEmitter.emit(WebsocketEstablishedEvent(serviceName, this@webSocket))

                logger.info("service $serviceName established a websocket . ")

                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            val resp = converter?.deserialize(Charsets.UTF_8, typeInfo<ApiWebSocketPacket>(), frame)

                            if (resp != null) {
                                resp as ApiWebSocketPacket

                                emitter.emit(WebsocketApiEvent(resp))
                            }
                        }

                        else -> Unit
                    }
                }

                logger.info("Service $serviceName closed.")
            }

            serviceEmitter.emit(WebSocketListening())
        }
}

suspend fun DefaultWebSocketServerSession.sendPacket(apiWebSocketPacket: ApiWebSocketPacket) {
    sendSerialized(apiWebSocketPacket)
}
