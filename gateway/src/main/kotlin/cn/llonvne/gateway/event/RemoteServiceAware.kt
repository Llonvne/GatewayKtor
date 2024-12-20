package cn.llonvne.gateway.event

import cn.llonvne.gateway.RemoteService
import cn.llonvne.service.abc.Service

/**
 * 表示一组 [RemoteService] 已经由 [cn.llonvne.service.ApiInsightService] 准备好以发送到 [cn.llonvne.gateway.ServiceInstaller] 来安装.
 *
 * 该事件是服务安装事件 [ServiceInstallEvent] 的子事件，用于指示 [cn.llonvne.gateway.ServiceInstaller]
 * 安装内含的 [RemoteService]。事件的触发表明这些服务已经准备好，等待被安装到系统中。
 *
 * **注意：** 你通常不应该直接订阅该事件，因为该事件中的 [RemoteService] 服务仍处于未安装的状态。
 * 如果你需要对 [RemoteService] 进行处理或监听，请订阅 [ServiceInstalledEvent]，并检查服务类型是否为 [RemoteService]。
 *
 * @param remoteService 一组已经准备好的 [RemoteService] 服务实例，它们尚未被安装，等待通过 [cn.llonvne.gateway.ServiceInstaller] 进行安装
 */
data class RemoteServiceAware(
    val remoteService: List<RemoteService>,
) : GatewayServiceEvent,
    ServiceInstallEvent {
    override val services: List<Service> = remoteService
}
