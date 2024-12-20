package cn.llonvne.gateway.config

import cn.llonvne.gateway.ApiWebSocketPacket
import cn.llonvne.gateway.Ping
import cn.llonvne.gateway.event.GatewayEventsCentral
import cn.llonvne.service.AliveDetectService
import cn.llonvne.service.ApiCallService
import cn.llonvne.service.ApiDescriptorService
import cn.llonvne.service.ApiInsightService
import cn.llonvne.service.ApiRouteService
import cn.llonvne.service.ApiWebsocketService
import cn.llonvne.service.BootUpService
import cn.llonvne.service.abc.GatewayService
import cn.llonvne.service.abc.Service
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import io.ktor.util.AttributeKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import java.time.Duration
import kotlin.time.toKotlinDuration

/**
 * ApiGatewayConfig 类是用于配置和管理API网关核心组件的中心类。
 * 它包含了异步协程范围、事件中心、服务端点配置、HTTP客户端设定、基础服务注册、
 * 配置获取器、WebSocket选项、日志级别以及API根路径等关键属性和方法。
 */
class ApiGatewayConfig {
    /**
     * 协程作用域插件变量，用于管理与插件相关的协程操作。
     * 此作用域默认使用 [kotlinx.coroutines.Dispatchers.IO] 调度器，并结合 [kotlinx.coroutines.SupervisorJob] 以确保子任务失败时不影响其他任务。
     */
    val pluginScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    /**
     * 中央事件管理器，负责分发和收集整个API网关的各种事件。
     * 实例化自 `GatewayEventsCentral` 类，利用给定的 [pluginScope] 初始化，
     * 以便在特定的协程作用域中管理事件流，支持 GatewayEvent、ServiceEvent 以及 ApiEvent 的发布与订阅。
     */
    val eventsCentral = GatewayEventsCentral(pluginScope)

    /**
     * API 网关服务的基础路径。
     * 此路径作为网关内部服务路由的前缀，用以区分不同类型的路由和服务。
     */
    var gateWayServiceApiRoot = "/gateway"

    /**
     * apiInsightHttpClient 是一个 HttpClient 实例，配置了内容协商以支持 JSON 格式，
     * 并启用了请求重试机制以便在网络问题时自动重试获取服务洞察信息。
     * 当请求重试时，会记录警告日志，说明服务洞察信息获取失败的原因，并指出即将进行重试。
     *
     * 此客户端旨在与服务配置中的洞察URI配合使用，根据[GatewayServiceYamlConfig.insightUri]定义，
     * 从各个服务端点收集洞察信息。它利用了Coroutine的异步能力来处理网络请求及其重试逻辑。
     */
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

    /**
     * 基础服务列表，初始化了一系列核心服务对象，用于API路由、洞察、启动时处理、API调用、API描述管理、WebSocket支持以及存活检测等功能。
     * 每个服务都是[GatewayService]的实现，旨在支撑网关的核心功能和事件处理流程。
     *
     * - [ApiRouteService]：负责根据配置动态构建API路由。
     * - [ApiInsightService]：获取并处理API洞察信息，用于服务发现和配置。
     * - [BootUpService]：网关启动时服务，订阅API相关事件。
     * - [ApiCallService]：处理API调用逻辑。
     * - [ApiDescriptorService]：管理API描述信息。
     * - [ApiWebsocketService]：提供WebSocket连接的支持和服务。
     * - [AliveDetectService]：执行服务存活检测。
     *
     * 这些服务通过[eventsCentral]中心进行事件的发布与订阅，以协调和响应系统中的各种情况。
     */
    val baseServices =
        mutableListOf(
            ApiRouteService { eventsCentral.emit(it) },
            ApiInsightService(apiInsightHttpClient, serviceYamlConfigAttributeKey) { eventsCentral.emit(it) },
            BootUpService { handler ->
                eventsCentral.collect(handler)
            },
            ApiCallService(),
            ApiDescriptorService { eventsCentral.emit(it) },
            ApiWebsocketService({
                eventsCentral.emit(it)
            }, {
                eventsCentral.emit(it)
            }),
            AliveDetectService(),
        )

    /**
     * 配置获取器，用于加载网关配置信息。
     * 默认实现为 [GatewayConfigYamlGetter]，读取名为 "gateway.yaml" 的配置文件。
     * 允许替换为其他实现以适应不同的配置源。
     *
     * @property configGetter [GatewayConfigGetter] 的实例，负责获取 [GatewayYamlConfig] 配置对象。
     */
    var configGetter: GatewayConfigGetter = GatewayConfigYamlGetter("gateway.yaml")

    /**
     * WebSocket 配置项，用于设置与 WebSocket 通信相关的参数和行为。
     *
     * @property contentConverter 设置内容转换器，此处使用 Kotlinx 序列化与 JSON 解析器相结合的方式，
     *                           支持 `ApiWebSocketPacket` 及其子类（如 `Ping`）的序列化与反序列化。
     * @property pingPeriod 设置心跳检测周期为 15 秒，确保连接活跃且及时发现断线情况。
     * @property timeout 设定连接超时时间为 15 秒，超过此时间未收到响应则视为连接失败。
     * @property maxFrameSize 最大帧尺寸设为 `Long.MAX_VALUE`，几乎允许无限制的帧大小，依据实际需求可调整。
     * @property masking 关闭掩码使用，优化性能，通常在服务器端可以关闭此选项。
     */
    val webSocketOptions: WebSockets.WebSocketOptions.() -> Unit = {
        contentConverter =
            KotlinxWebsocketSerializationConverter(
                Json {
                    this.serializersModule =
                        SerializersModule {
                            polymorphic(ApiWebSocketPacket::class, Ping::class, Ping.serializer())
                        }
                },
            )

        pingPeriod = Duration.ofSeconds(15).toKotlinDuration()

        timeout = Duration.ofSeconds(15).toKotlinDuration()

        maxFrameSize = Long.MAX_VALUE

        masking = false
    }

    /**
     * WebSocket 的端点路径。
     * 所有与 WebSocket 相关的连接都应该通过这个路径进行。
     */
    val webSocketEndpoint = "/websocket"

    /**
     * 日志记录级别，用于配置API网关的日志输出详细程度。
     * 默认值为[Level.INFO]，表示记录信息级别的日志。
     */
    var level = Level.INFO

    /**
     * API 根路径配置变量。
     * 用于指定所有 API 路由的根路径前缀。
     */
    var apiRoot = "/api"

    internal val services: MutableList<Service> = mutableListOf()

    /**
     * 向 ApiGatewayConfig 中注册一个本地服务。
     *
     * @param service 要注册的本地服务实例。它必须实现 [Service] 接口。
     */
    fun registerLocalService(service: Service) {
        services.add(service)
    }
}
