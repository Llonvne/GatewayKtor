package cn.llonvne.gateway

import cn.llonvne.gateway.config.ApiGatewayConfig
import cn.llonvne.gateway.event.GatewayConfigAware
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.routing.routing

/**
 * 定义 [ApiGateway] 为 Ktor 的应用程序插件.
 *
 *
 */
val ApiGateway =
    createApplicationPlugin("ApiGateway", ::ApiGatewayConfig) {
        val config = pluginConfig

        val serviceInstaller = ServiceInstaller(config, application.routing { })

        serviceInstaller.installServices(config.baseServices + config.services)

        config.eventsCentral.emit(GatewayConfigAware(config.readConfigFromYaml()))

        configureCallLoggingForGateway(config)
    }
