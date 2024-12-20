package cn.llonvne.gateway

import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
sealed interface ApiWebSocketPacket

@Serializable
class Ping(
    val from: String,
    val time: Long = Instant.now().toEpochMilli(),
) : ApiWebSocketPacket
