package cn.llonvne.service.abc

import cn.llonvne.gateway.Constants
import cn.llonvne.gateway.type.ServiceOrder
import io.ktor.server.routing.Route

/**
 * Essential Service 将会被网关引导程序直接初始化并使用的服务.
 */
interface GatewayEssentialService : Service {
    // Essential Service Do NOT provide router.
    override fun route(): Route.() -> Unit = {}

    // Essential Service should be local.
    override val isRemote: Boolean get() = false

    override val order: ServiceOrder get() = Constants.ServiceOrderConstants.essentialServiceOrder
}
