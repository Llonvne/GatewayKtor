package cn.llonvne.gateway.event

import cn.llonvne.gateway.RemoteService
import cn.llonvne.gateway.type.ServiceId
import cn.llonvne.service.Service

interface ServiceEvent

sealed interface ServiceEventAction : ServiceEvent

open class ServiceAction(
    val serviceId: ServiceId,
    val action: (Service) -> Unit,
) : ServiceEventAction

class AllServiceAction(
    val action: (Service) -> Unit,
) : ServiceEventAction

class RemoteServiceInsightAction(
    serviceId: ServiceId,
    action: (RemoteService) -> Unit,
) : ServiceAction(
        serviceId,
        {
            if (it is RemoteService && it.name == serviceId.name) {
                action(it)
            }
        },
    )
