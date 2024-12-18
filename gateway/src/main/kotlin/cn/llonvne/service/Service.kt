package cn.llonvne.service

import cn.llonvne.gateway.Constants.ServiceOrderConstants.normalServiceOrder
import cn.llonvne.gateway.event.AllServiceAction
import cn.llonvne.gateway.event.GatewayEvent
import cn.llonvne.gateway.event.ServiceAction
import cn.llonvne.gateway.event.ServiceEvent
import cn.llonvne.gateway.event.ServiceEventAction
import cn.llonvne.gateway.event.TargetEvent
import cn.llonvne.gateway.type.ServiceOrder
import io.ktor.server.routing.Route
import kotlin.reflect.full.isSubclassOf

interface Service {
    val name: String

    fun route(): Route.() -> Unit

    val isRemote: Boolean

    val order: ServiceOrder get() = normalServiceOrder

    fun type(): String {
        if (this is GatewayEssentialService) {
            return "Essential"
        }

        return if (isRemote) {
            return "Remote"
        } else {
            return "Local"
        }
    }

    fun collect(e: ServiceEvent) {
        if (e is ServiceEventAction) {
            when (e) {
                is AllServiceAction -> e.action(this)
                is ServiceAction -> {
                    if (e.serviceId.name == name) {
                        e.action(this)
                    }
                }
            }
        }
    }
}

/**
 * 用于在 Service 类中处理特定类型的 GatewayEvent。
 *
 * 请总是使用该函数进行事件类型判断。事件匹配逻辑可能会变得更加复杂，
 * 该函数保证始终能准确匹配事件类型与处理器。
 *
 * 该函数首先检查传入事件的类型是否符合预期，并在必要时根据目标进一步判断。
 * 如果事件是 TargetEvent 类型的子类，则会检查该事件的目标是否是当前 Service 实例。
 * 只有当事件类型匹配且目标符合要求时，处理器才会被执行。
 *
 * **注意**：只有在事件类型和目标都匹配时，事件才会被处理。
 * 这可以确保事件只会被处理一次，并避免误处理其他类型的事件。
 *
 * @param e 需要处理的 GatewayEvent 实例。
 * @param processor 事件处理器，接受类型为 `E` 的事件进行处理。
 *
 * @see TargetEvent 进一步的目标检查
 * @see GatewayEvent 事件类型的基类
 */
inline fun <reified E : GatewayEvent> Service.process(e: GatewayEvent, processor: (E) -> Unit) {
    if (e !is E) {
        return
    }

    if (e::class.isSubclassOf(TargetEvent::class)) {
        e as TargetEvent<*>
        if (!e.isTarget(this)) {
            return
        }
    }
    processor(e)
}
