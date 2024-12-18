package cn.llonvne.gateway.config

import kotlinx.serialization.Serializable

@Serializable
data class GatewayYamlConfig(
    val services: List<GatewayServiceYamlConfig> = emptyList(),
    val routes: List<ApiRouteConfig> = emptyList(),
)

@Serializable
data class GatewayServiceYamlConfig(
    val name: String,
    val url: String,
    val essential: Boolean = false,
    val insightUri: String? = "/api",
    val supportInsight: Boolean = true,
)

@Serializable
data class ApiRouteConfig(
    val from: String,
    val to: String,
)
