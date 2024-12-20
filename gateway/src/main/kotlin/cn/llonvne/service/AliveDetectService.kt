package cn.llonvne.service

import cn.llonvne.gateway.event.ServiceEvent
import cn.llonvne.gateway.type.Emitter
import cn.llonvne.service.abc.GatewayService

class AliveDetectService(
    val emitter: Emitter<ServiceEvent>,
) : GatewayService {
    override val name: String = "AliveDetectService"

}
