package cn.llonvne.gateway.event

import cn.llonvne.gateway.ApiWebSocketPacket
import io.ktor.server.websocket.DefaultWebSocketServerSession

sealed interface ApiEvent

class WebsocketApiEvent(
    val packet: ApiWebSocketPacket,
) : ApiEvent

class WebsocketEstablishedEvent(
    val service: String,
    val session: DefaultWebSocketServerSession,
) : ApiEvent
