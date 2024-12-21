package cn.llonvne.service

import cn.llonvne.gateway.Constants
import cn.llonvne.gateway.event.ApiEvent
import cn.llonvne.gateway.event.ServiceEvent
import cn.llonvne.gateway.event.SubscribeApiEvent
import cn.llonvne.gateway.type.EventSubscriber
import cn.llonvne.gateway.type.ServiceOrder
import cn.llonvne.service.abc.GatewayService

class BootUpService(
    val subscribe: EventSubscriber<ApiEvent>,
) : GatewayService {
    override val name: String = "GatewayBootUpService"

    override val isRemote: Boolean = false

    override val order: ServiceOrder = Constants.ServiceOrderConstants.bootUpServiceOrder

    override suspend fun collect(e: ServiceEvent) {
        process<SubscribeApiEvent>(e) {
            subscribe.subscribe(it.handler)
        }
    }
}
