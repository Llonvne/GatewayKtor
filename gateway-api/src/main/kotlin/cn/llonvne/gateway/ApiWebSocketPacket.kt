package cn.llonvne.gateway

import kotlinx.serialization.Serializable

@Serializable
sealed interface ApiWebSocketPacket

@Serializable
class Ping() : ApiWebSocketPacket