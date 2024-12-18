package cn.llonvne.gateway

import cn.llonvne.gateway.type.ServiceOrder
import cn.llonvne.service.type.RemoteServiceContext
import io.ktor.util.AttributeKey

object Constants {
    object ServiceOrderConstants {
        val normalServiceOrder = ServiceOrder(100)

        val gatewayServiceOrder = ServiceOrder(0)

        val essentialServiceOrder = ServiceOrder(-100)
    }

    object AttributeKeys {
        val remoteServiceAttributeKey = AttributeKey<RemoteServiceContext>("remoteServiceContext")
    }
}
