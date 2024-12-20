package cn.llonvne.gateway

import cn.llonvne.gateway.config.ApiGatewayConfig
import cn.llonvne.gateway.event.GatewayConfigAware
import cn.llonvne.gateway.event.WebSocketInstalled
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.install
import io.ktor.server.routing.createRouteFromPath
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import kotlinx.coroutines.flow.FlowCollector
import org.slf4j.LoggerFactory

/**
 * 定义 [ApiGateway] 为 Ktor 的应用程序插件.
 */
val ApiGateway =
    createApplicationPlugin("ApiGateway", ::ApiGatewayConfig) {
        val config = pluginConfig

        val logger = LoggerFactory.getLogger("Gateway")

        val routingRoot = application.routing {}

        val serviceInstaller = ServiceInstaller(config, routingRoot)

        serviceInstaller.installServices(config.baseServices + config.services)

        config.eventsCentral.emit(GatewayConfigAware(config.configGetter.get()))

        application.install(WebSockets, configure = config.webSocketOptions)

        val webSocketRoot = routingRoot.createRouteFromPath(config.webSocketEndpoint)

        config.eventsCentral.emit(WebSocketInstalled(webSocketRoot))

        configureCallLoggingForGateway(config)
    }
