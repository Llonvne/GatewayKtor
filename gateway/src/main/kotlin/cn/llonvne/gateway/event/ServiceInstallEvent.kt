package cn.llonvne.gateway.event

import cn.llonvne.service.Service

/**
 * 表示事件内部的 [services] 需要被 [cn.llonvne.gateway.ServiceInstaller] 处理并添加至服务列表
 *
 * 该事件类型是抽象的，用于表示服务安装事件，具体的事件实现可以通过子类化该接口，
 * 来表达特定的服务安装行为。这些事件将由 [cn.llonvne.gateway.ServiceInstaller] 处理，
 * 并将服务添加到网关的服务列表中，以便在网关的服务管理系统中进行注册与管理。
 *
 * 每当事件中的服务被成功安装后，系统将发射 [cn.llonvne.gateway.event.ServiceInstalledEvent]，表示服务已成功添加到服务列表中并准备就绪。
 *
 * @see cn.llonvne.gateway.ServiceInstaller 负责处理该事件并安装服务到网关服务列表中
 * @see ServiceInstalledEvent 服务安装成功后发射的事件，表示服务已准备就绪
 */
interface ServiceInstallEvent : GatewayServiceEvent {
    val services: List<Service>
}

