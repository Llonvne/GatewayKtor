package cn.llonvne.service.abc

import cn.llonvne.gateway.Constants.ServiceOrderConstants.normalServiceOrder
import cn.llonvne.gateway.event.AllServiceAction
import cn.llonvne.gateway.event.ApiEvent
import cn.llonvne.gateway.event.ServiceAction
import cn.llonvne.gateway.event.ServiceEvent
import cn.llonvne.gateway.event.ServiceEventAction
import cn.llonvne.gateway.type.Emitter
import cn.llonvne.gateway.type.ServiceOrder
import io.ktor.server.routing.Route

/**
 * 定义了服务的基本行为和属性，是所有服务实现的基础接口。
 * 服务可以是本地（Local）或远程（Remote），并且能够响应特定类型的服务事件和服务动作。
 *
 * 接口集成了[ServiceEventEmitterAware]和[ApiEventEmitterAware]，
 * 表明服务能够感知服务事件和API事件，并提供了默认的事件处理实现。
 *
 * @property name 服务名称，用于标识服务的唯一标识符。
 * @property isRemote 标记服务是否为远程服务，默认实现提供获取该状态的方法。
 * @property order 获取服务的启动顺序，默认为[normalServiceOrder]。
 *
 * @see ServiceEventEmitterAware 服务事件感知能力。
 * @see ApiEventEmitterAware API事件感知能力。
 * @see ServiceOrder 服务启动顺序定义。
 */
interface Service : ServiceEventEmitterAware, ApiEventEmitterAware {
    val name: String

    fun route(): Route.() -> Unit

    val isRemote: Boolean

    val order: ServiceOrder get() = normalServiceOrder

    override fun serviceEventEmitterAware(emitter: Emitter<ServiceEvent>) {}

    override fun apiEventEmitterAware(emitter: Emitter<ApiEvent>) {}

    fun type(): String =
        when {
            this is GatewayEssentialService -> "Essential"
            this is GatewayService -> "GatewayService"
            isRemote -> "Remote"
            else -> "Local"
        }

    suspend fun collect(e: ServiceEvent) {
        if (e !is ServiceEventAction) return

        executeActionIfApplicable(e)
    }

    suspend fun collect(e: ApiEvent) {}

    private suspend fun executeActionIfApplicable(event: ServiceEventAction) {
        when (event) {
            is AllServiceAction -> event.action(this)
            is ServiceAction -> {
                if (event.serviceId.name != name) return
                event.action(this)
            }
        }
    }
}
