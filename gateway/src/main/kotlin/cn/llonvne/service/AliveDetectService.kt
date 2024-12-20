package cn.llonvne.service

import cn.llonvne.gateway.event.GatewayEvent
import cn.llonvne.service.abc.GatewayService
import org.slf4j.LoggerFactory

class AliveDetectService : GatewayService {
    override val name: String = "AliveDetectService"

    private val logger = LoggerFactory.getLogger(this::class.java)

    override suspend fun collect(gatewayEvent: GatewayEvent) {
    }
}
