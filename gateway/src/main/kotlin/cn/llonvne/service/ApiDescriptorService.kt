package cn.llonvne.service

import cn.llonvne.gateway.event.RemoteServiceAction
import cn.llonvne.gateway.type.Emitter
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

class ApiDescriptorService(
    val emitter: Emitter<RemoteServiceAction>,
) : GatewayService {
    override val name: String = "ApiDescriptorService"

    override fun route(): Route.() -> Unit =
        {
        }
}
