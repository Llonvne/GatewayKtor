package cn.llonvne.service.abc

import cn.llonvne.gateway.Constants.ServiceOrderConstants.normalServiceOrder
import cn.llonvne.gateway.event.AllServiceAction
import cn.llonvne.gateway.event.ServiceAction
import cn.llonvne.gateway.event.ServiceEvent
import cn.llonvne.gateway.event.ServiceEventAction
import cn.llonvne.gateway.type.ServiceOrder
import io.ktor.server.routing.Route

interface Service {
    val name: String

    fun route(): Route.() -> Unit

    val isRemote: Boolean

    val order: ServiceOrder get() = normalServiceOrder

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
