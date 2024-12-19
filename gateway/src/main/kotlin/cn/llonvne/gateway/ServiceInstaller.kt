package cn.llonvne.gateway

import cn.llonvne.gateway.config.ApiGatewayConfig
import cn.llonvne.gateway.event.GatewayEvent
import cn.llonvne.gateway.event.ServiceInstallEvent
import cn.llonvne.gateway.event.ServiceInstalledEvent
import cn.llonvne.service.GatewayEssentialService
import cn.llonvne.service.GatewayService
import cn.llonvne.service.Service
import cn.llonvne.service.process
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingRoot
import io.ktor.server.routing.createRouteFromPath
import io.ktor.server.routing.route
import kotlinx.coroutines.flow.FlowCollector
import org.slf4j.LoggerFactory

class ServiceInstaller(
    val config: ApiGatewayConfig,
    val root: RoutingRoot,
) : GatewayEssentialService {
    override val name: String = "ServiceInstaller"

    private val logger = LoggerFactory.getLogger(this::class.java)

    init {
        config.eventsCentral.collect(FlowCollector<GatewayEvent> {
            collect(it)
        })
    }

    private fun collect(gatewayEvent: GatewayEvent) = process<ServiceInstallEvent>(gatewayEvent) {
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

        logger.info("GatewayService[${gatewayService.type()}] ${gatewayService.name} has established!")

        return route
    }

    private fun installNormalService(service: Service): Route {

        val route = root.route(config.apiRoot, service.route())

        logger.info("Service[${service.type()}] ${service.name} has established!")

        return route
    }

    private fun installRemoteService(service: RemoteService): Route {
        logger.info("Service[${service.type()}] ${service.name} has established!")
        // do nothing, real routing will be processed by ApiRouteService
        return root.createRouteFromPath(config.apiRoot)
    }

    fun installService(service: Service) {
        config.eventsCentral.collect(service::collect)

        val route = when (service) {
            is GatewayService -> installGatewayService(service)
            is RemoteService -> installRemoteService(service)
            else -> installNormalService(service)
        }

        config.eventsCentral.emit(ServiceInstalledEvent(service, route))
    }

    fun installServices(services: List<Service>) {
        services.forEach { installService(it) }
    }
}
