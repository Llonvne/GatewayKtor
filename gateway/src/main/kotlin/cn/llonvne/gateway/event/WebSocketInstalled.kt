package cn.llonvne.gateway.event

import io.ktor.server.routing.Route

/**
 * 表示 WebSocket 协议已安装的事件。
 *
 * 当 WebSocket 服务配置完成并且安装成功后，系统会触发此事件。
 * 这意味着网关现在支持 WebSocket 连接和通信功能。
 *
 * 继承自 [GatewayEvent]，属于网关管理事件范畴。
 */
class WebSocketInstalled(
    val webSocketRoot: Route,
) : GatewayEvent
