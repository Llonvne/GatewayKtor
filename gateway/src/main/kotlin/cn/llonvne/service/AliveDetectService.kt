package cn.llonvne.service

import cn.llonvne.gateway.RemoteService
import cn.llonvne.gateway.event.GatewayEvent
import cn.llonvne.gateway.event.ServiceEvent
import cn.llonvne.gateway.event.ServiceInstalledEvent
import cn.llonvne.gateway.event.SubscribeApiEvent
import cn.llonvne.gateway.event.WebSocketListening
import cn.llonvne.gateway.type.Emitter
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.time.Duration
import kotlin.time.toKotlinDuration

class AliveDetectService(
    val httpClient: HttpClient,
    val emitter: Emitter<ServiceEvent>,
) : GatewayService {
    override val name: String = "AliveDetectService"

    override suspend fun collect(gatewayEvent: GatewayEvent) {
        process<ServiceInstalledEvent>(gatewayEvent) {
            if (it.service is RemoteService) {
                requestWebsocketIn(it.service, 30)
            }
        }
    }

    override suspend fun collect(e: ServiceEvent) {
        process<WebSocketListening>(e) {
            emitter.emit(SubscribeApiEvent {
                println(it)
            })
        }
    }

    private suspend fun requestWebsocketIn(service: RemoteService, seconds: Long) {
        withContext(Dispatchers.IO) {
            httpClient.get(service.config.url + "/gateway")
        }
    }
}