package cn.llonvne.service

import cn.llonvne.gateway.RemoteService
import cn.llonvne.gateway.event.ApiCallEvent
import cn.llonvne.gateway.event.GatewayEvent
import cn.llonvne.gateway.event.ServiceInstalledEvent
import cn.llonvne.gateway.type.Emitter
import cn.llonvne.service.ApiCallService.Companion.toHttpMethod
import cn.llonvne.service.abc.GatewayService
import cn.llonvne.service.type.RemoteServiceContext
import io.ktor.server.routing.route

class ApiRouteService(
    val emitter: Emitter<ApiCallEvent>,
) : GatewayService {
    override val name: String = "ApiRouteService"

    override val isRemote: Boolean = false

    override suspend fun collect(gatewayEvent: GatewayEvent) =
        process<ServiceInstalledEvent>(gatewayEvent) {
            if (it.service !is RemoteService) {
                return@process
            }

            val insight = it.service.insight
            val config = it.service.config

            it.route.route(insight.namespace) {
                insight.apis.forEach { apiDescriptor ->

                    val context = RemoteServiceContext(apiDescriptor, insight, config)

                    route(apiDescriptor.remoteUri, apiDescriptor.method.toHttpMethod()) {
                        handle {
                            val sendEvent = ApiCallEvent(context, this)
                            emitter.emit(sendEvent)
                            sendEvent.wait()
                        }
                    }
                }
            }
        }
}
