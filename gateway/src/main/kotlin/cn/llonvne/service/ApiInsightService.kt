package cn.llonvne.service

import cn.llonvne.gateway.ApiInsightRequest
import cn.llonvne.gateway.ApiInsightResponse
import cn.llonvne.gateway.RemoteService
import cn.llonvne.gateway.config.GatewayServiceYamlConfig
import cn.llonvne.gateway.config.GatewayYamlConfig
import cn.llonvne.gateway.event.GatewayConfigAware
import cn.llonvne.gateway.event.GatewayEvent
import cn.llonvne.gateway.event.RemoteServiceAware
import cn.llonvne.gateway.event.ServiceEvent
import cn.llonvne.gateway.event.SubscribeApiEvent
import cn.llonvne.gateway.event.WebSocketListening
import cn.llonvne.gateway.event.WebsocketApiEvent
import cn.llonvne.gateway.event.WebsocketEstablishedEvent
import cn.llonvne.gateway.type.Emitter
import cn.llonvne.service.abc.GatewayService
import io.ktor.util.AttributeKey
import kotlinx.coroutines.isActive
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ApiInsightService(
    private val emitter: Emitter<RemoteServiceAware>,
    private val subscriber: Emitter<SubscribeApiEvent>,
) : GatewayService {
    override val name: String = "ApiInsightService"

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    private lateinit var gatewayYamlConfig: GatewayYamlConfig

    override suspend fun collect(gatewayEvent: GatewayEvent) {
        process<GatewayConfigAware>(gatewayEvent) {
            gatewayYamlConfig = it.config
        }
    }

    override suspend fun collect(serviceEvent: ServiceEvent) {
        process<WebSocketListening>(serviceEvent) {
            subscriber.emit(
                SubscribeApiEvent {
                    when (it) {
                        is WebsocketApiEvent ->
                            when (it.packet) {
                                is ApiInsightResponse -> processApiInsightResponse(it.packet)
                                else -> Unit
                            }

                        is WebsocketEstablishedEvent -> processWebsocketEstablishedEvent(it)
                    }
                },
            )
        }
    }

    private suspend fun processWebsocketEstablishedEvent(event: WebsocketEstablishedEvent) {
        with(event.session) {
            if (!this.isActive) {
                logger.error("${event.service} websocket is closed. Failed to send API insight request.")
                return
            }
            sendPacket(ApiInsightRequest())
        }
    }

    private fun processApiInsightResponse(resp: ApiInsightResponse) {
        emitter.emit(
            RemoteServiceAware(
                listOf(
                    RemoteService(
                        resp.serviceInsight,
                        config = gatewayYamlConfig.services.find { it.name == resp.serviceInsight.name }!!,
                    ),
                ),
            ),
        )
    }
}
