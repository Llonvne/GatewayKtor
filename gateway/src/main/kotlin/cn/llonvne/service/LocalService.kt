package cn.llonvne.service

import cn.llonvne.service.abc.Service
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

class LocalService : Service {
    override val name: String = "TestService"

    override fun route(): Route.() -> Unit =
        {
            get("/name") { call.respondText(name) }
        }

    override val isRemote: Boolean = false
}
