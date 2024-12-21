package cn.llonvne.gateway.event

import cn.llonvne.gateway.RemoteService
import cn.llonvne.gateway.type.ServiceId
import cn.llonvne.service.abc.Service

interface ServiceEvent

sealed interface ServiceEventAction : ServiceEvent

open class ServiceAction(
    val serviceId: ServiceId,
    val action: (Service) -> Unit,
) : ServiceEventAction

open class AllServiceAction(
    val action: suspend (Service) -> Unit,
) : ServiceEventAction

class RemoteServiceAction(
    action: suspend (RemoteService) -> Unit,
) : AllServiceAction({
        if (it is RemoteService) {
            action(it)
        }
    })

class WebSocketListening : ServiceEvent

class IsServiceAlive(
    val serviceId: ServiceId,
) : ServiceEvent
