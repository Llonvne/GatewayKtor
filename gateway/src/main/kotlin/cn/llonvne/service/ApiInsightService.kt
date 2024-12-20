package cn.llonvne.service

import cn.llonvne.gateway.RemoteService
import cn.llonvne.gateway.ServiceInsight
import cn.llonvne.gateway.config.GatewayServiceYamlConfig
import cn.llonvne.gateway.event.GatewayConfigAware
import cn.llonvne.gateway.event.GatewayEvent
import cn.llonvne.gateway.event.RemoteServiceAware
import cn.llonvne.gateway.type.Emitter
import cn.llonvne.service.abc.GatewayService
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.util.AttributeKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ApiInsightService(
    private val insightClient: HttpClient,
    private val key: AttributeKey<GatewayServiceYamlConfig>,
    private val emitter: Emitter<RemoteServiceAware>,
) : GatewayService {
    class FailedToGetApiInsight(
        val config: GatewayServiceYamlConfig,
        val e: Throwable,
    ) : Exception()

    override val name: String = "ApiInsightService"

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    /**
     * 处理接收到的网关事件。如果事件类型是 `GatewayServiceYamlConfigReady`，则从中提取配置并尝试获取远程服务的 Insight 数据。
     * 获取成功的远程服务将通过 `GatewayRemoteServicesReady` 事件发送出去。
     *
     * 该函数将在 IO 调度器上运行，并并行处理每个服务的 Insight 获取操作。若某个服务的 Insight 获取失败，则该服务会被排除在结果之外。
     *
     * @param gatewayEvent 网关事件，可能包含配置数据。只有当事件类型为 `GatewayServiceYamlConfigReady` 时，才会处理该事件。
     */
    override suspend fun collect(gatewayEvent: GatewayEvent) {
        val e =
            when (gatewayEvent) {
                is GatewayConfigAware -> gatewayEvent
                else -> return
            }

        withContext(Dispatchers.IO + SupervisorJob()) {
            val remoteServices =
                e.config.services
                    .filter { it.supportInsight }
                    .map { config ->
                        async { fetchInsightForService(config) }
                    }.awaitAll()
                    .filterNotNull()
            emitter.emit(RemoteServiceAware(remoteServices))
        }
    }

    /**
     * 尝试为指定的服务配置获取 Insight 数据。如果获取失败，并且该服务是关键服务，则抛出异常。
     * 否则，记录错误并返回 null。
     *
     * @param config 服务配置，包含服务的 URL、Insight URI 和是否为关键服务等信息。
     *
     * @return 如果获取成功，返回包含 Insight 数据和服务配置的 `RemoteService` 对象；
     *         如果获取失败且服务为非关键服务，返回 `null`；
     *         如果获取失败且服务为关键服务，抛出 `FailedToGetApiInsight` 异常。
     */
    private suspend fun fetchInsightForService(config: GatewayServiceYamlConfig): RemoteService? =
        try {
            val insight = fetchInsight(insightClient, config)
            RemoteService(insight, config)
        } catch (e: FailedToGetApiInsight) {
            logInsightFailure(config, e)
            if (config.essential) throw e else null
        }

    /**
     * 使用指定的 HTTP 客户端获取指定服务的 Insight 数据。
     * 如果获取过程中发生错误，则抛出 `FailedToGetApiInsight` 异常。
     *
     * @param httpClient 用于发送 HTTP 请求的客户端实例。
     * @param config 包含服务 URL 和 Insight URI 配置的对象。
     *
     * @return 获取到的 `ServiceInsight` 数据。
     *
     * @throws FailedToGetApiInsight 如果在获取 Insight 数据的过程中发生任何异常，抛出该异常。
     */
    private suspend fun fetchInsight(
        httpClient: HttpClient,
        config: GatewayServiceYamlConfig,
    ): ServiceInsight =
        try {
            httpClient
                .get(config.url + config.insightUri) {
                    attributes.put(key, config)
                }.body()
        } catch (e: Exception) {
            throw FailedToGetApiInsight(config, e)
        }

    private fun logInsightFailure(
        config: GatewayServiceYamlConfig,
        e: FailedToGetApiInsight,
    ) {
        if (config.essential) {
            logger.error(
                "Failed to fetch insight for the essential service '${config.name}' from '${config.url + config.insightUri}'. This failure prevents the gateway from starting.",
            )
            throw e
        } else {
            logger.error("Failed to fetch insight for the service ${config.name} from '${config.url + config.insightUri}")
            null
        }
    }

    override val isRemote: Boolean = false
}
