package cn.llonvne.gateway.event

import cn.llonvne.gateway.config.GatewayYamlConfig

/**
 * 表示 [cn.llonvne.gateway.event.GatewayConfigAware] 已经被读取
 *
 * 该事件一般由 [cn.llonvne.gateway.ApiGateway] 直接触发，用于告知系统网关的配置信息已经被正确加载，
 * 并可以被其他组件用于初始化、路由配置、服务管理等相关操作。
 */
data class GatewayConfigAware(
    val config: GatewayYamlConfig,
) : GatewayConfigEvent