package cn.llonvne.gateway.event

import cn.llonvne.service.Service
import io.ktor.server.routing.Route

/**
 * 表示该网关服务已经被 [cn.llonvne.gateway.ServiceInstaller] 成功安装
 *
 * 当一个服务通过 [cn.llonvne.gateway.ServiceInstaller] 成功安装并注册到网关后，系统会触发该事件，
 * 用于通知其他系统组件该服务已经准备就绪并可以正常使用。
 *
 * 该事件主要用于通知服务已经成功注册，确保服务的生命周期得到了适当的管理，
 * 并允许其他组件开始与该服务交互。
 *
 * @param service 成功安装并注册到网关的服务实例
 * @param route 该服务的 API 接口路由，表示该服务的请求处理入口
 *
 */
data class ServiceInstalledEvent(
    val service: Service,
    val route: Route
) : GatewayServiceEvent