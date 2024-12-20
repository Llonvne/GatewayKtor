package cn.llonvne.gateway.event

import cn.llonvne.gateway.RemoteService
import cn.llonvne.gateway.type.ServiceId
import cn.llonvne.service.abc.Service

interface ServiceEvent

sealed interface ServiceEventAction : ServiceEvent

open class ServiceAction(
    val serviceId: ServiceId,
    val action: (Service) -> Unit,
) : ServiceEventAction

open class AllServiceAction(
    val action: suspend (Service) -> Unit,
) : ServiceEventAction

class RemoteServiceAction(
    action: suspend (RemoteService) -> Unit,
) : AllServiceAction({
    if (it is RemoteService) {
        action(it)
    }
})

class WebSocketListening : ServiceEvent

/**
 * 订阅API事件类。
 *
 * 此类允许用户订阅并定义对[ApiEvent]的处理逻辑。当特定的API事件发生时，
 * 通过传入的lambda表达式(handler)来执行相应的处理代码。
 *
 * @property handler 事件处理器，接收一个[ApiEvent]类型的参数，用于定义事件处理逻辑。
 *                  当此类实例被作为事件触发时，对应的handler会被执行。
 *
 * 示例用法（非KDoc格式）:
 * ```kotlin
 * val myEventHandler = { event: ApiEvent ->
 *     // 在此处实现你的事件处理逻辑
 * }
 * val subscribeEvent = SubscribeApiEvent(myEventHandler)
 * ```
 *
 * 注意：该类是一个事件的包装器，用于注册处理函数，并自身作为一个事件可被GatewayEventsCentral发布和订阅。
 */
class SubscribeApiEvent(
    val handler: suspend (ApiEvent) -> Unit,
) : ServiceEvent

class IsServiceAlive(val serviceId: ServiceId) : ServiceEvent