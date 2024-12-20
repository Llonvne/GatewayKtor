package cn.llonvne.service.type

import cn.llonvne.gateway.ApiDescriptor
import cn.llonvne.gateway.ServiceInsight
import cn.llonvne.gateway.config.GatewayServiceYamlConfig

/**
 * 远程服务调用上下文类，封装了进行API调用所需的全部配置信息。
 *
 * @property apiDescriptor [ApiDescriptor] 当前API描述，包含名称、本地及远程URI、描述、HTTP方法和内容类型等信息。
 * @property insight [ServiceInsight] 服务洞察信息，提供服务的基本URI、命名空间以及相关API描述列表。
 * @property config [GatewayServiceYamlConfig] 网关服务的YAML配置，包含服务名、基础URL、是否为核心服务、洞察URI、是否支持洞察及WebSocket URI等配置项。
 */
class RemoteServiceContext(
    val apiDescriptor: ApiDescriptor,
    val insight: ServiceInsight,
    val config: GatewayServiceYamlConfig,
)
