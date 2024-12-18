package cn.llonvne.gateway

import cn.llonvne.gateway.config.GatewayServiceYamlConfig
import cn.llonvne.service.Service
import io.ktor.server.routing.Route

class RemoteService(
    val insight: ServiceInsight,
    val config: GatewayServiceYamlConfig,
    override val name: String = insight.name,
) : Service {
    /**
     * 路由函数将由 [cn.llonvne.service.ApiRouteService] 进行处理。
     *
     * 请注意， [ServiceInstaller] 将忽略此 [route] 函数的返回值。
     *
     * **重要说明**：此函数仅作为路由配置的占位符，不应在此处提供任何路由定义或逻辑。所有路由处理应由 [ApiRouteService] 负责完成。
     */
    override fun route(): Route.() -> Unit = throw NotImplementedError()

    override val isRemote: Boolean = true
}
