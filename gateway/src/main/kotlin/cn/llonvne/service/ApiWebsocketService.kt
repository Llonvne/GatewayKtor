package cn.llonvne.service

import cn.llonvne.gateway.ApiWebSocketPacket
import cn.llonvne.gateway.event.GatewayEvent
import cn.llonvne.gateway.event.WebSocketInstalled
import cn.llonvne.gateway.event.WebSocketListening
import cn.llonvne.gateway.event.WebsocketApiEvent
import cn.llonvne.gateway.type.Emitter
import io.ktor.server.websocket.converter
import io.ktor.server.websocket.webSocket
import io.ktor.util.reflect.typeInfo
import io.ktor.websocket.Frame


class ApiWebsocketService(
    val emitter: Emitter<WebsocketApiEvent>,
    val serviceEmitter: Emitter<WebSocketListening>
) : GatewayService {
    override val name: String = "ApiWebsocketService"

    override suspend fun collect(gatewayEvent: GatewayEvent) =
        process<WebSocketInstalled>(gatewayEvent) {
            val root = it.webSocketRoot

            root.webSocket("/api") {
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
            }

            serviceEmitter.emit(WebSocketListening())
        }
}
