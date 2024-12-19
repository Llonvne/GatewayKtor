package cn.llonvne.service

import cn.llonvne.gateway.event.ApiEvent
import cn.llonvne.gateway.event.ServiceEvent
import cn.llonvne.gateway.event.SubscribeApiEvent
import cn.llonvne.gateway.type.EventSubscriber

class BootUpService(
    val subscribe: EventSubscriber<ApiEvent>,
) : GatewayService {
    override val name: String = "GatewayBootUpService"

    override val isRemote: Boolean = false

    override suspend fun collect(e: ServiceEvent) {
        process<SubscribeApiEvent>(e) {
            subscribe.subscribe(it.handler)
        }
    }
}
