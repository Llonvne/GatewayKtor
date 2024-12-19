package cn.llonvne.gateway.config

import cn.llonvne.gateway.event.GatewayEventsCentral
import cn.llonvne.service.ApiCallService
import cn.llonvne.service.ApiDescriptorService
import cn.llonvne.service.ApiInsightService
import cn.llonvne.service.ApiRouteService
import cn.llonvne.service.BootUpService
import cn.llonvne.service.GatewayService
import cn.llonvne.service.Service
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.AttributeKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.slf4j.event.Level

class ApiGatewayConfig {
    // ASYNC COROUTINE SCOPE
    val pluginScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // EVENT
    val eventsCentral = GatewayEventsCentral(pluginScope)

    var gateWayServiceApiRoot = "/gateway"

    // API INSIGHT HTTP CLIENT
    var apiInsightHttpClient =
        HttpClient(CIO) {
            install(ContentNegotiation) {
                this.json(Json)
            }
            install(HttpRequestRetry) {
                retryOnException(maxRetries = 3)
                exponentialDelay()

                modifyRequest {
                    val req = it
                    val context = req.attributes.getOrNull(serviceYamlConfigAttributeKey) ?: return@modifyRequest
                    LoggerFactory.getLogger(this@modifyRequest::class.java).warn(
                        "Unable to fetch insight for the service '${context.name}' from '${context.url + context.insightUri}'. " +
                                "Reason: ${this.cause?.localizedMessage}. A retry will be attempted shortly.",
                    )
                }
            }
        }

    // API INSIGHT CONFIG ATTRIBUTE KEY
    var serviceYamlConfigAttributeKey = AttributeKey<GatewayServiceYamlConfig>("serviceConfigKey")


    // BASE SERVICES
    val baseServices: MutableList<GatewayService> =
        mutableListOf(
            ApiRouteService { eventsCentral.emit(it) },
            ApiInsightService(apiInsightHttpClient, serviceYamlConfigAttributeKey) { eventsCentral.emit(it) },
            BootUpService(),
            ApiCallService(),
            ApiDescriptorService { eventsCentral.emit(it) }
        )

    // CONFIG
    var configGetter: GatewayConfigGetter = GatewayConfigYamlGetter("gateway.yaml")


    // LOGGING
    var level = Level.INFO

    var apiRoot = "/api"

    internal val services: MutableList<Service> = mutableListOf()

    fun registerLocalService(service: Service) {
        services.add(service)
    }
}
