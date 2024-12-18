package cn.llonvne.gateway

import kotlinx.serialization.Serializable

@Serializable
enum class HttpMethod {
    GET,
    POST,
    DELETE,
    PATCH,
}
