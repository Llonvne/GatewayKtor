package cn.llonvne.service

import cn.llonvne.gateway.ApiDescriptor
import io.ktor.server.routing.RoutingContext

data class ApiDescriptorWithAction(
    val apiDescriptor: ApiDescriptor,
    val action:suspend RoutingContext.() -> Unit
)
