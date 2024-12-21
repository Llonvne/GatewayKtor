package cn.llonvne.service.abc

import cn.llonvne.gateway.event.ApiEvent
import cn.llonvne.gateway.event.ServiceEvent
import cn.llonvne.gateway.type.Emitter

/**
 * 服务基类，实现了[Service]接口，提供了API事件和服务事件的发射器感知能力的默认实现。
 * 子类可以通过覆写具体方法来扩展服务功能，同时必须初始化抽象属性以确保事件处理的正常运作。
 *
 * @property apiEventEmitter API事件发射器，用于发射API相关事件。
 * @property serviceEmitter 服务事件发射器，用于发射服务相关事件。
 *
 * @see Service 服务基础接口，定义了服务的核心行为和属性。
 * @see Emitter 事件发射器接口，用于向订阅者广播特定类型的事件。
 */
abstract class ServiceBase : Service {
    protected lateinit var apiEventEmitter: Emitter<ApiEvent>

    protected lateinit var serviceEmitter: Emitter<ServiceEvent>

    final override fun apiEventEmitterAware(emitter: Emitter<ApiEvent>) {
        apiEventEmitter = emitter
    }

    final override fun serviceEventEmitterAware(emitter: Emitter<ServiceEvent>) {
        serviceEmitter = emitter
    }
}
