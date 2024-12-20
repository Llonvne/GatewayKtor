package cn.llonvne.gateway

import cn.llonvne.gateway.config.GatewayServiceYamlConfig
import cn.llonvne.service.abc.Service
import io.ktor.server.routing.Route

/**
 * 代表一个远程服务，继承自 [Service] 类，提供了远程服务的相关功能。
 *
 * [cn.llonvne.service.ApiInsightService] 通过该实例将请求进行路由和转发，确保请求可以正确地到达
 * 配置中指定的实际服务或处理器。通常，该类用于充当请求的代理，提供对远程服务的访问和管理。
 *
 * @property insight 服务的 API 详细信息和可见性，由 [ServiceInsight] 提供。
 * @property config 网关服务的配置详情，包括服务的 URL 和其他相关配置，由 [GatewayServiceYamlConfig] 提供。
 * @property name 服务的名称，默认使用 [insight] 中的名称。如果没有提供，将自动从 [insight] 获取名称。
 */
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
