package cn.llonvne.gateway

import cn.llonvne.gateway.config.ApiGatewayConfig
import cn.llonvne.gateway.event.GatewayEvent
import cn.llonvne.gateway.event.ServiceInstallEvent
import cn.llonvne.gateway.event.ServiceInstalledEvent
import cn.llonvne.service.abc.GatewayEssentialService
import cn.llonvne.service.abc.GatewayService
import cn.llonvne.service.abc.Service
import cn.llonvne.service.process
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingRoot
import io.ktor.server.routing.createRouteFromPath
import io.ktor.server.routing.route
import kotlinx.coroutines.flow.FlowCollector
import org.slf4j.LoggerFactory

/**
 * ServiceInstaller 类负责安装和配置服务到 API 网关中。它根据服务类型（网关服务、远程服务或普通服务），
 * 采用不同的安装策略，确保服务能够正确接入并响应请求。该类是 GatewayEssentialService 的实现。
 *
 * @property config ApiGatewayConfig 即 API 网关的配置信息，用于获取路由根、事件中心等配置项。
 * @property root RoutingRoot 路由根对象，用于在其中添加或配置服务路由。
 *
 */
class ServiceInstaller(
    val config: ApiGatewayConfig,
    val root: RoutingRoot,
) : GatewayEssentialService {
    override val name: String = "ServiceInstaller"

    private val logger = LoggerFactory.getLogger(this::class.java)

    init {
        config.eventsCentral.collect(
            FlowCollector<GatewayEvent> {
                collect(it)
            },
        )
    }

    private fun collect(gatewayEvent: GatewayEvent) =
        process<ServiceInstallEvent>(gatewayEvent) {
            val remoteServices = it.services.sortedBy { it.order }
            remoteServices.forEach {
                installService(it)
            }
        }

    private fun installGatewayService(gatewayService: GatewayService): Route {
        val route = root.route(config.gateWayServiceApiRoot, gatewayService.route())

        config.eventsCentral.collect { it: GatewayEvent ->
            gatewayService.collect(it)
        }
        return route
    }

    private fun installNormalService(service: Service): Route {
        val route = root.route(config.apiRoot, service.route())

        return route
    }

    private fun installRemoteService(service: RemoteService): Route {
        // do nothing, real routing will be processed by ApiRouteService
        return root.createRouteFromPath(config.apiRoot)
    }

    fun installService(service: Service) {
        config.eventsCentral.collect(service::collect)

        val route =
            when (service) {
                is GatewayService -> installGatewayService(service)
                is RemoteService -> installRemoteService(service)
                else -> installNormalService(service)
            }

        config.eventsCentral.emit(ServiceInstalledEvent(service, route))
        logger.info("Installed service [${service.type()}]${service.name}")
    }

    fun installServices(services: List<Service>) {
        services.sortedBy { it.order }.forEach { installService(it) }
    }
}
