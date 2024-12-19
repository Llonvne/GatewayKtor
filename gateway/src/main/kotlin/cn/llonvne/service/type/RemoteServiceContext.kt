package cn.llonvne.service.type

import cn.llonvne.gateway.ApiDescriptor
import cn.llonvne.gateway.ServiceInsight
import cn.llonvne.gateway.config.GatewayServiceYamlConfig

class RemoteServiceContext(
    val apiDescriptor: ApiDescriptor,
    val insight: ServiceInsight,
    val config: GatewayServiceYamlConfig,
)
