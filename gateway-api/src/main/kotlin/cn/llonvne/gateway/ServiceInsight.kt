package cn.llonvne.gateway

import kotlinx.serialization.Serializable

@Serializable
class ServiceInsight(
    val name: String,
    val baseUri: String,
    val namespace: String,
    val apis: List<ApiDescriptor>,
)

@Serializable
class ApiDescriptor(
    val name: String,
    val localUri: String,
    val remoteUri: String = localUri,
    val description: String,
    val method: HttpMethod,
    val contentType: String,
)
