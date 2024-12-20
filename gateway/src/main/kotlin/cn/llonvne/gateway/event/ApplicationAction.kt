package cn.llonvne.gateway.event

import io.ktor.server.application.Application

data class ApplicationAction(
    val action: Application.() -> Unit,
) : GatewayEvent
