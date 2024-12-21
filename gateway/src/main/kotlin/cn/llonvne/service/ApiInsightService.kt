package cn.llonvne.service

import cn.llonvne.gateway.ApiInsightRequest
import cn.llonvne.gateway.ApiInsightResponse
import cn.llonvne.gateway.RemoteService
import cn.llonvne.gateway.event.ApiEvent
import cn.llonvne.gateway.event.RemoteServiceAware
import cn.llonvne.gateway.event.WebsocketApiEvent
import cn.llonvne.gateway.event.WebsocketEstablishedEvent
import cn.llonvne.service.abc.GatewayServiceBase
import kotlinx.coroutines.isActive
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ApiInsightService : GatewayServiceBase() {
    override val name: String = "ApiInsightService"

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override suspend fun collect(e: ApiEvent) {
        super.collect(e)
        when (e) {
            is WebsocketApiEvent ->
                when (e.packet) {
                    is ApiInsightResponse -> processApiInsightResponse(e.packet)
                    else -> Unit
                }

            is WebsocketEstablishedEvent -> processWebsocketEstablishedEvent(e)
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
        gatewayEventEmitter.emit(
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
