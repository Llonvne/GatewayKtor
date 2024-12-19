package cn.llonvne.service

import cn.llonvne.gateway.event.RemoteServiceAction
import cn.llonvne.gateway.type.Emitter
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlinx.coroutines.flow.channelFlow

class ApiDescriptorService(
    val emitter: Emitter<RemoteServiceAction>
) : GatewayService {
    override val name: String = "ApiDescriptorService"

    override fun route(): Route.() -> Unit = {
        get("/descriptor") {
            val flowsApis = channelFlow {
                emitter.emit(RemoteServiceAction({
                    send(it.insight)
                }))
            }

            flowsApis.collect {
                println(it)
            }
        }
    }
}