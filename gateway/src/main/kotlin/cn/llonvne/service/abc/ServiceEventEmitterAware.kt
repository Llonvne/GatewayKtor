package cn.llonvne.service.abc

import cn.llonvne.gateway.event.ServiceEvent
import cn.llonvne.gateway.type.Emitter


/**
 * 实现此接口以表明一个类可以感知并处理[ServiceEvent]事件。
 * 通过提供一个[Emitter]实例，可以使对象具备发射服务事件的能力，
 * 进而与其他组件通过事件机制进行通信，实现解耦的系统架构。
 *
 * 实现类需要定义`serviceEventEmitterAware`方法，该方法接收一个[Emitter<ServiceEvent>]参数，
 * 并在内部注册或者存储此发射器以供后续使用，比如在特定业务逻辑中触发事件。
 *
 * 具体服务实现通常无需关心该接口，使用 [ServiceBase.serviceEmitter] 可直接发射[ServiceEvent]事件
 *
 * @see Emitter 用于发射特定类型事件的接口。
 * @see ServiceEvent 服务事件的根接口，所有自定义服务事件应基于此接口。
 */
interface ServiceEventEmitterAware {
    fun serviceEventEmitterAware(
        emitter: Emitter<ServiceEvent>
    )
}