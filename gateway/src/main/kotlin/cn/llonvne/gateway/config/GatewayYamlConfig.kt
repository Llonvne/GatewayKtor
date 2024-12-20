package cn.llonvne.gateway.config

import kotlinx.serialization.Serializable

@Serializable
data class GatewayYamlConfig(
    val services: List<GatewayServiceYamlConfig> = emptyList(),
    val routes: List<ApiRouteConfig> = emptyList(),
    val persistence: GatewayPersistentConfig
)

@Serializable
data class GatewayServiceYamlConfig(
    val name: String,
    val url: String,
    val essential: Boolean = false,
    val insightUri: String? = "/api",
    val supportInsight: Boolean = true,
    val websocketUri: String = "/websocket",
)

@Serializable
data class ApiRouteConfig(
    val from: String,
    val to: String,
)


@Serializable
data class GatewayPersistentConfig(
    val redis: GatewayRedisConfig,
    val db: GatewayDbConfig
)

@Serializable
data class GatewayRedisConfig(
    val url: String,
    val username: String? = null,
    val password: String? = null
)

enum class GatewaySupportDbType {
    PostgreSql
}

@Serializable
data class GatewayDbConfig(
    val type: GatewaySupportDbType,
    val url: String,
    val username: String,
    val password: String
)

