package cn.llonvne.gateway.event

import cn.llonvne.service.type.RemoteServiceContext
import io.ktor.server.routing.RoutingContext
import kotlinx.coroutines.channels.Channel

/**
 * 表示一次网关处理调用转发的过程。
 *
 * 该事件通常由 [cn.llonvne.service.ApiRouteService] 在匹配到注册的路径和方法时触发，并异步等待事件的处理 [wait]。
 *
 * 事件的回应通常由 [cn.llonvne.service.ApiCallService] 执行，具体回应的内容由 [routingContext] 完成，
 * 并且在回应完成后，通过调用 [ok] 来通知 [cn.llonvne.service.ApiRouteService] 表示处理完成。
 *
 * 该事件表示一次网关 API 调用的发生。它携带了与 API 调用相关的上下文信息，包括：
 * - [context]：包含当前 API 调用的远程服务上下文 ([RemoteServiceContext])，用于提供相关的 API 描述和配置信息。
 * - [routingContext]：提供当前路由的上下文 ([RoutingContext])，用于处理 API 路由及相关响应。
 *
 * 该事件内含有一个通道（[channel]），用于处理异步响应。事件通过该通道来同步等待处理结果，
 * 并在处理完成后通知其他组件调用已成功（通过 [ok] 方法）或继续等待处理（通过 [wait] 方法）。
 *
 */
data class ApiCallEvent(
    val context: RemoteServiceContext,
    val routingContext: RoutingContext,
) : GatewayServiceEvent {
    val channel: Channel<Unit> = Channel<Unit>()
    suspend fun wait() {
        channel.receive()
    }

    suspend fun ok() {
        channel.send(Unit)
        channel.close()
    }
}