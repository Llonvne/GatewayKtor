package cn.llonvne.service

import cn.llonvne.gateway.ApiWebSocketPacket
import cn.llonvne.gateway.event.GatewayEvent
import cn.llonvne.gateway.event.UninstallRemoteService
import cn.llonvne.gateway.event.WebSocketInstalled
import cn.llonvne.gateway.event.WebSocketListening
import cn.llonvne.gateway.event.WebsocketApiEvent
import cn.llonvne.gateway.event.WebsocketEstablishedEvent
import cn.llonvne.service.abc.GatewayServiceBase
import io.ktor.server.request.header
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.converter
import io.ktor.server.websocket.sendSerialized
import io.ktor.server.websocket.webSocket
import io.ktor.util.reflect.typeInfo
import io.ktor.websocket.Frame
import io.ktor.websocket.closeExceptionally
import org.slf4j.LoggerFactory

class ApiWebsocketService : GatewayServiceBase() {
    override val name: String = "ApiWebsocketService"

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val servicesNameSet by lazy { gatewayYamlConfig.services.map { it.name }.toSet() }

    override suspend fun collectGateway(gatewayEvent: GatewayEvent) =
        process<WebSocketInstalled>(gatewayEvent) {

            val root = it.webSocketRoot

            root.webSocket(gatewayYamlConfig.serviceWebsocketEndpoint) {
                val serviceName = call.request.header(gatewayYamlConfig.serviceNameHeader)
                if (serviceName == null ||
                    serviceName !in servicesNameSet
                ) {
                    closeExceptionally(RuntimeException())
                    return@webSocket
                }

                apiEventEmitter.emit(WebsocketEstablishedEvent(serviceName, this@webSocket))

                logger.info("service $serviceName established a websocket . ")

                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            val resp = converter?.deserialize(Charsets.UTF_8, typeInfo<ApiWebSocketPacket>(), frame)

                            if (resp != null) {
                                resp as ApiWebSocketPacket

                                apiEventEmitter.emit(WebsocketApiEvent(resp))
                            }
                        }

                        else -> Unit
                    }
                }

                logger.info("Service $serviceName closed.")
                gatewayEventEmitter.emit(UninstallRemoteService(serviceName))
            }

            serviceEmitter.emit(WebSocketListening())
        }
}

suspend fun DefaultWebSocketServerSession.sendPacket(apiWebSocketPacket: ApiWebSocketPacket) {
    sendSerialized(apiWebSocketPacket)
}
