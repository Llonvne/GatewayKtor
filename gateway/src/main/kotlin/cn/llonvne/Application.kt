package cn.llonvne

import cn.llonvne.gateway.ApiGateway
import cn.llonvne.service.LocalService
import io.ktor.server.application.Application
import io.ktor.server.application.install

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain
        .main(args)
}

fun Application.module() {
    configureSecurity()

    install(ApiGateway) {
        registerLocalService(LocalService())
    }
}
