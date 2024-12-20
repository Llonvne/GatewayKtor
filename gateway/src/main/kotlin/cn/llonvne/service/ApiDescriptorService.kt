package cn.llonvne.service

import cn.llonvne.gateway.event.RemoteServiceAction
import cn.llonvne.gateway.type.Emitter
import cn.llonvne.service.abc.GatewayService
import io.ktor.server.routing.Route

class ApiDescriptorService(
    val emitter: Emitter<RemoteServiceAction>,
) : GatewayService {
    override val name: String = "ApiDescriptorService"

    override fun route(): Route.() -> Unit =
        {
        }
}
