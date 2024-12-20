package cn.llonvne.gateway.event

import cn.llonvne.gateway.ApiWebSocketPacket

sealed interface ApiEvent

class WebsocketApiEvent(
    val packet: ApiWebSocketPacket,
) : ApiEvent
