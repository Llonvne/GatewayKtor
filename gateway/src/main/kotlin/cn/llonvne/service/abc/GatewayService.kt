package cn.llonvne.service.abc

import cn.llonvne.gateway.Constants
import cn.llonvne.gateway.event.GatewayEvent
import cn.llonvne.gateway.type.Emitter
import cn.llonvne.gateway.type.ServiceOrder
import io.ktor.server.routing.Route

interface GatewayService :
    Service,
    GatewayEventEmitterAware {
    /**
     * 允许 GatewayService 处理 GatewayEvent。
     *
     * **重要说明**：请不要直接使用该函数进行事件类型判断，除非你知道你在做什么。
     * 总是使用 [cn.llonvne.service.process] 函数来处理事件类型判断，这样可以确保正确地处理事件。
     *
     * @param gatewayEvent 传入的网关事件，通常是与网关操作相关的事件。
     *
     * @see cn.llonvne.gateway.event.TargetEvent
     */
    suspend fun collect(gatewayEvent: GatewayEvent) {}

    override val order: ServiceOrder
        get() = Constants.ServiceOrderConstants.gatewayServiceOrder

    override fun route(): Route.() -> Unit = {}

    override val isRemote: Boolean get() = false

    override fun gatewayEventEmitterAware(emitter: Emitter<GatewayEvent>) {}
}
